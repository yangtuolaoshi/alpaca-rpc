package love.ytlsnb.rpc.protocol.coder;

import io.vertx.core.buffer.Buffer;
import love.ytlsnb.rpc.model.RPCRequest;
import love.ytlsnb.rpc.model.RPCResponse;
import love.ytlsnb.rpc.protocol.AlpacaProtocol;
import love.ytlsnb.rpc.protocol.enums.ProtocolSerializerEnum;
import love.ytlsnb.rpc.protocol.enums.ProtocolTypeEnum;
import love.ytlsnb.rpc.serializer.Serializer;
import love.ytlsnb.rpc.serializer.SerializerFactory;

import java.io.IOException;

import static love.ytlsnb.rpc.protocol.constants.ProtocolConstant.MAGIC;

/**
 * 解码器
 */
public class ProtocolDecoder {
    /**
     * 解码：将Buffer转换为协议数据报对象
     * @param buffer Buffer
     * @return 协议数据报对象
     */
    public static AlpacaProtocol<?> decode(Buffer buffer) {
        if (buffer == null) {
            throw new RuntimeException("数据为空");
        }
        // 先拿到首部
        AlpacaProtocol.Header header = new AlpacaProtocol.Header();
        byte magicByte = buffer.getByte(0);
        if (magicByte != MAGIC) {
            throw new RuntimeException("魔数非法");
        }
        header.setMagic(magicByte);// 魔数
        header.setVersion(buffer.getByte(1));// 版本号
        byte serializerByte = buffer.getByte(2);// 序列化器
        header.setSerializer(serializerByte);
        byte typeByte = buffer.getByte(3);
        header.setType(typeByte);// 类型
        header.setStatus(buffer.getByte(4));// 状态码
        header.setRequestId(buffer.getLong(5));// 请求ID
        header.setDataLength(buffer.getInt(13));// 数据段长度
        // 解决粘包问题，先只拿部分数据
        byte[] dataBytes = buffer.getBytes(17, 17 + header.getDataLength());// 数据段数据，用字节数组存储
        // 拿到序列化器
        ProtocolSerializerEnum serializerEnum = ProtocolSerializerEnum.getEnumBySerializer(serializerByte);
        if (serializerEnum == null) {
            throw new RuntimeException("序列化器不存在");
        }
        String serializerName = serializerEnum.getName();
        Serializer serializer = SerializerFactory.getSerializer(serializerName);
        // 根据请求类型对请求进行序列化
        ProtocolTypeEnum protocolTypeEnum = ProtocolTypeEnum.getEnumByType(typeByte);
        if (protocolTypeEnum == null) {
            throw new RuntimeException("非法的请求类型");
        }
        int type = protocolTypeEnum.getType();
        try {
            if (type == 1) {// 这是一个请求
                AlpacaProtocol<RPCRequest> alpacaProtocol = new AlpacaProtocol<>();
                alpacaProtocol.setHeader(header);
                RPCRequest rpcRequest = serializer.deserialize(dataBytes, RPCRequest.class);
                alpacaProtocol.setBody(rpcRequest);
                return alpacaProtocol;
            } else if (type == 2) {// 这是一个响应
                AlpacaProtocol<RPCResponse> alpacaProtocol = new AlpacaProtocol<>();
                alpacaProtocol.setHeader(header);
                RPCResponse rpcResponse = serializer.deserialize(dataBytes, RPCResponse.class);
                alpacaProtocol.setBody(rpcResponse);
                return alpacaProtocol;
            } else if (type == 3) {// 这是一个心跳检测
                AlpacaProtocol<Object> alpacaProtocol = new AlpacaProtocol<>();
                alpacaProtocol.setHeader(header);
                return alpacaProtocol;
            } else {
                throw new RuntimeException("这是一个非法的消息类型");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
