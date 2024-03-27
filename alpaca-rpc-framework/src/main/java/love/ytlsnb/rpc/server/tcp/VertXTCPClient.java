package love.ytlsnb.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.rpc.RPCApplication;
import love.ytlsnb.rpc.config.RPCConfig;
import love.ytlsnb.rpc.model.RPCRequest;
import love.ytlsnb.rpc.model.RPCResponse;
import love.ytlsnb.rpc.protocol.AlpacaProtocol;
import love.ytlsnb.rpc.protocol.coder.ProtocolDecoder;
import love.ytlsnb.rpc.protocol.coder.ProtocolEncoder;
import love.ytlsnb.rpc.protocol.constants.ProtocolConstant;
import love.ytlsnb.rpc.protocol.enums.ProtocolSerializerEnum;
import love.ytlsnb.rpc.protocol.enums.ProtocolTypeEnum;
import love.ytlsnb.rpc.registry.model.ClientMetaInfo;
import love.ytlsnb.rpc.serializer.Serializer;
import love.ytlsnb.rpc.serializer.SerializerFactory;
import love.ytlsnb.rpc.server.tcp.decorator.TCPBufferHandlerDecorator;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * TCP协议客户端
 */
@Slf4j
public class VertXTCPClient {
    /**
     * 发送请求
     * @param rpcRequest RPC请求对象
     * @param clientMetaInfo 服务注册元数据
     * @return 响应数据
     */
    public static RPCResponse doRequest(RPCRequest rpcRequest, ClientMetaInfo clientMetaInfo) throws Exception {
        RPCConfig rpcConfig = RPCApplication.getRpcConfig();
        String clientAddress = clientMetaInfo.getClientAddress();
        // 创建协议对象
        AlpacaProtocol.Header header = new AlpacaProtocol.Header();
        header.setMagic(ProtocolConstant.MAGIC);
        header.setType((byte) ProtocolTypeEnum.REQUEST.getType());
        header.setVersion(ProtocolConstant.VERSION);
        header.setSerializer((byte) ProtocolSerializerEnum.getSerializerByName(rpcConfig.getSerializer()));
        header.setRequestId(IdUtil.getSnowflakeNextId());// 随机生一个ID
        AlpacaProtocol<RPCRequest> alpacaProtocol = new AlpacaProtocol<>();
        alpacaProtocol.setHeader(header);
        alpacaProtocol.setBody(rpcRequest);
        // 发送TCP请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        String[] addressSplit = clientAddress.split(":");
        CompletableFuture<RPCResponse> completableFuture = new CompletableFuture<>();
        netClient.connect(Integer.parseInt(addressSplit[1]), addressSplit[0], result -> {
            if (result.succeeded()) {
                log.info("连接成功");
                // 在这里发送TCP请求
                NetSocket socket = result.result();
                try {
                    // 编码
                    Buffer buffer = ProtocolEncoder.encode(alpacaProtocol);
                    // 发送请求
                    socket.write(buffer);
                    // 响应处理
                    TCPBufferHandlerDecorator tcpBufferHandlerDecorator = new TCPBufferHandlerDecorator(respBuffer -> {
                        // 解码
                        AlpacaProtocol<RPCResponse> response = (AlpacaProtocol<RPCResponse>) ProtocolDecoder.decode(respBuffer);
                        completableFuture.complete(response.getBody());
                    });
                    socket.handler(tcpBufferHandlerDecorator);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log.error("连接失败！原因为 {}", result.cause().toString());
            }
        });
        RPCResponse rpcResponse = completableFuture.get();
        netClient.close();
        return rpcResponse;
    }
}
