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
import love.ytlsnb.rpc.loadbalancer.LoadBalancer;
import love.ytlsnb.rpc.loadbalancer.LoadBalancerFactory;
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
import love.ytlsnb.rpc.retry.Retry;
import love.ytlsnb.rpc.retry.RetryFactory;
import love.ytlsnb.rpc.serializer.Serializer;
import love.ytlsnb.rpc.serializer.SerializerFactory;
import love.ytlsnb.rpc.server.tcp.VertXTCPClient;
import love.ytlsnb.rpc.server.tcp.decorator.TCPBufferHandlerDecorator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
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
     * @param clientClass 接口的类对象
     * @param <T> 接口的类型
     * @return 代理对象
     */
    public static <T> T getProxyObj(Class<T> clientClass) {
        return (T) Proxy.newProxyInstance(
                clientClass.getClassLoader(),
                new Class[]{clientClass},
                (Object proxy, Method method, Object[] args) -> {
                    // 在这里发送请求，调用目标对象
                    // 通过配置文件获取指定序列化器
                    RPCConfig rpcConfig = RPCApplication.getRpcConfig();
                    RPCRequest rpcRequest = RPCRequest.builder()
                            .clientName(method.getDeclaringClass().getSimpleName())
                            .methodName(method.getName())
                            .params(args)
                            .paramTypes(method.getParameterTypes())
                            .build();
                    // 通过配置文件来获取指定注册中心
                    RegistryConfig registryConfig = RPCApplication.getRegistryConfig();
                    String registryName = registryConfig.getRegistryName();
                    Registry registry = RegistryFactory.getRegistry(registryName);
                    List<ClientMetaInfo> clientMetaInfos =
                            registry.find(clientClass.getSimpleName() + ":" + DEFAULT_REGISTRY_VERSION);
                    if (clientMetaInfos == null || clientMetaInfos.size() == 0) {
                        throw new RuntimeException("没有可用的服务");
                    }
                    // 拿负载均衡器
                    String loadBalancerName = rpcConfig.getLoadbalancer();
                    LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer(loadBalancerName);
                    HashMap<String, Object> requestParams = new HashMap<>();
                    requestParams.put("alpaca", method.getName());
                    ClientMetaInfo clientMetaInfo = loadBalancer.select(requestParams, clientMetaInfos);
                    String clientAddress = clientMetaInfo.getClientAddress();
                    System.out.println("-----------------------------");
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    log.error("这次请求的地址是 {}", clientAddress);
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    System.out.println("-----------------------------");
                    // 拿到重试策略
                    Retry retry = RetryFactory.getRetry(rpcConfig.getRetry());
//                    RPCResponse rpcResponse = completableFuture.get();
                    // 发送TCP请求并返回结果
                    return retry.doRetry(() -> VertXTCPClient.doRequest(rpcRequest, clientMetaInfo)).getData();
                }
        );
    }
}
