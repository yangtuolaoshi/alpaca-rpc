package love.ytlsnb.rpc.server.tcp;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import love.ytlsnb.rpc.model.RPCRequest;
import love.ytlsnb.rpc.model.RPCResponse;
import love.ytlsnb.rpc.protocol.AlpacaProtocol;
import love.ytlsnb.rpc.protocol.coder.ProtocolDecoder;
import love.ytlsnb.rpc.protocol.coder.ProtocolEncoder;
import love.ytlsnb.rpc.protocol.enums.ProtocolTypeEnum;
import love.ytlsnb.rpc.registry.local.LocalRegistry;
import love.ytlsnb.rpc.server.tcp.decorator.TCPBufferHandlerDecorator;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP服务器请求处理器
 */
public class TCPServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        TCPBufferHandlerDecorator tcpBufferHandlerDecorator = new TCPBufferHandlerDecorator(buffer -> {
            RPCResponse rpcResponse = new RPCResponse();
            // 解码
            AlpacaProtocol<RPCRequest> request = (AlpacaProtocol<RPCRequest>) ProtocolDecoder.decode(buffer);
            RPCRequest rpcRequest = request.getBody();
            // 调用服务，得到返回值
            Class<?> implClass = LocalRegistry.get(rpcRequest.getClientName());
            Method method;
            try {
                method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                Object respBody = method.invoke(implClass.newInstance(), rpcRequest.getParams());
                rpcResponse.setData(respBody);
                rpcResponse.setMessage("success");
                rpcResponse.setDataType(method.getReturnType());
            } catch (Exception e) {
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
                e.printStackTrace();
            }
            // 构造响应数据
            AlpacaProtocol<RPCResponse> response = new AlpacaProtocol<>();
            AlpacaProtocol.Header header = request.getHeader();
            header.setType((byte) ProtocolTypeEnum.RESPONSE.getType());
            response.setHeader(header);
            response.setBody(rpcResponse);
            try {
                // 编码
                Buffer respBuffer = ProtocolEncoder.encode(response);
                // 返回数据
                netSocket.write(respBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        netSocket.handler(tcpBufferHandlerDecorator);
    }
}
