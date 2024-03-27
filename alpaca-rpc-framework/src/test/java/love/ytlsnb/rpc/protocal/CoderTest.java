package love.ytlsnb.rpc.protocal;

import io.vertx.core.buffer.Buffer;
import love.ytlsnb.rpc.model.RPCRequest;
import love.ytlsnb.rpc.protocol.AlpacaProtocol;
import love.ytlsnb.rpc.protocol.coder.ProtocolDecoder;
import love.ytlsnb.rpc.protocol.coder.ProtocolEncoder;
import love.ytlsnb.rpc.serializer.Serializer;
import love.ytlsnb.rpc.serializer.SerializerFactory;
import org.junit.Test;

import java.io.IOException;

/**
 * 编码 / 解码器测试
 */
public class CoderTest {
    @Test
    public void testCoder() throws IOException {
        AlpacaProtocol<RPCRequest> alpacaProtocol = new AlpacaProtocol<>();
        AlpacaProtocol.Header header = new AlpacaProtocol.Header();
        header.setMagic((byte) 1);
        header.setVersion((byte) 1);
        header.setType((byte) 1);
        header.setSerializer((byte) 2);
        header.setStatus((byte) 0);
        header.setRequestId(1L);
        RPCRequest rpcRequest = new RPCRequest();
        rpcRequest.setClientName("UserClient");
        rpcRequest.setClientVersion("1.0");
        rpcRequest.setMethodName("getUser");
        rpcRequest.setParams(new Object[]{});
        rpcRequest.setParamTypes(new Class[]{});
        Serializer serializer = SerializerFactory.getSerializer("json");
        byte[] rpcRequestBytes = serializer.serialize(rpcRequest);
        header.setDataLength(rpcRequestBytes.length);
        alpacaProtocol.setHeader(header);
        alpacaProtocol.setBody(rpcRequest);
        // 编码
        Buffer buffer = ProtocolEncoder.encode(alpacaProtocol);
        // 解码
        AlpacaProtocol<?> alpacaProtocol1 = ProtocolDecoder.decode(buffer);
        System.out.println("编码前：" + alpacaProtocol);
        System.out.println("解码后：" + alpacaProtocol1);
    }
}
