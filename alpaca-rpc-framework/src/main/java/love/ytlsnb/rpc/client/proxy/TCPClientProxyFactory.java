package love.ytlsnb.rpc.client.proxy;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.rpc.RPCApplication;
import love.ytlsnb.rpc.config.RPCConfig;
import love.ytlsnb.rpc.config.RegistryConfig;
import love.ytlsnb.rpc.model.RPCRequest;
import love.ytlsnb.rpc.model.RPCResponse;
import love.ytlsnb.rpc.protocol.AlpacaProtocol;
import love.ytlsnb.rpc.protocol.coder.ProtocolDecoder;
import love.ytlsnb.rpc.protocol.coder.ProtocolEncoder;
import love.ytlsnb.rpc.protocol.constants.ProtocolConstant;
import love.ytlsnb.rpc.protocol.enums.ProtocolSerializerEnum;
import love.ytlsnb.rpc.protocol.enums.ProtocolTypeEnum;
import love.ytlsnb.rpc.registry.Registry;
import love.ytlsnb.rpc.registry.RegistryFactory;
import love.ytlsnb.rpc.registry.model.ClientMetaInfo;
import love.ytlsnb.rpc.serializer.Serializer;
import love.ytlsnb.rpc.serializer.SerializerFactory;
import love.ytlsnb.rpc.server.tcp.decorator.TCPBufferHandlerDecorator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static love.ytlsnb.rpc.constant.RPCConstant.DEFAULT_REGISTRY_VERSION;

/**
 * TCP客户端请求处理器代理工厂
 */
@Slf4j
public class TCPClientProxyFactory {
    /**
     * 拿到代理对象
     * @param clientClass
     * @param <T>
     * @return
     */
    public static <T> T getProxyObj(Class<?> clientClass) {
        return (T) Proxy.newProxyInstance(
                clientClass.getClassLoader(),
                new Class[]{clientClass},
                (Object proxy, Method method, Object[] args) -> {
                    CompletableFuture<RPCResponse> completableFuture = new CompletableFuture<>();
                    // 在这里发送请求，调用目标对象
                    // 1. 构造请求对象并序列化
                    // 通过配置文件获取指定序列化器
                    RPCConfig rpcConfig = RPCApplication.getRpcConfig();
                    String serializerName = rpcConfig.getSerializer();
                    Serializer serializer = SerializerFactory.getSerializer(serializerName);
                    RPCRequest rpcRequest = RPCRequest.builder()
                            .clientName(method.getDeclaringClass().getSimpleName())
                            .methodName(method.getName())
                            .params(args)
                            .paramTypes(method.getParameterTypes())
                            .build();
                    byte[] reqBytes = serializer.serialize(rpcRequest);
                    // 2. 发送请求
                    // 通过配置文件来获取指定注册中心
                    RegistryConfig registryConfig = RPCApplication.getRegistryConfig();
                    String registryName = registryConfig.getRegistryName();
                    Registry registry = RegistryFactory.getRegistry(registryName);
                    List<ClientMetaInfo> clientMetaInfos =
                            registry.find(clientClass.getSimpleName() + ":" + DEFAULT_REGISTRY_VERSION);
                    if (clientMetaInfos == null || clientMetaInfos.size() == 0) {
                        throw new RuntimeException("没有可用的服务");
                    }
                    // TODO 暂时先取第一个
                    ClientMetaInfo clientMetaInfo = clientMetaInfos.get(0);
                    String clientAddress = clientMetaInfo.getClientAddress();
                    // 发送TCP请求
                    Vertx vertx = Vertx.vertx();
                    NetClient netClient = vertx.createNetClient();
                    String[] addressSplit = clientAddress.split(":");
                    netClient.connect(Integer.parseInt(addressSplit[1]), addressSplit[0], result -> {
                        if (result.succeeded()) {
                            log.info("连接成功");
                            // 在这里发送TCP请求
                            NetSocket socket = result.result();
                            // 创建协议对象
                            AlpacaProtocol.Header header = new AlpacaProtocol.Header();
                            header.setMagic(ProtocolConstant.MAGIC);
                            header.setType((byte) ProtocolTypeEnum.REQUEST.getType());
                            header.setVersion(ProtocolConstant.VERSION);
                            header.setSerializer((byte) ProtocolSerializerEnum.getSerializerByName(rpcConfig.getSerializer()));
                            header.setRequestId(IdUtil.getSnowflakeNextId());// 随机生一个ID
                            header.setDataLength(reqBytes.length);
                            AlpacaProtocol<RPCRequest> alpacaProtocol = new AlpacaProtocol<>();
                            alpacaProtocol.setHeader(header);
                            alpacaProtocol.setBody(rpcRequest);
                            try {
                                // 编码
                                Buffer buffer = ProtocolEncoder.encode(alpacaProtocol);
                                // 发送请求
                                socket.write(buffer);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // 响应处理
                            TCPBufferHandlerDecorator tcpBufferHandlerDecorator = new TCPBufferHandlerDecorator(buffer -> {
                                // 解码
                                AlpacaProtocol<RPCResponse> response = (AlpacaProtocol<RPCResponse>) ProtocolDecoder.decode(buffer);
                                completableFuture.complete(response.getBody());
                            });
                            socket.handler(tcpBufferHandlerDecorator);
                        } else {
                            log.error("连接失败！原因为 {}", result.cause());
                        }
                    });
                    RPCResponse rpcResponse = completableFuture.get();
                    // 关闭连接
                    netClient.close();
                    return rpcResponse.getData();
                }
        );
    }
}
