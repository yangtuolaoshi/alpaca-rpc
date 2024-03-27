package love.ytlsnb.rpc.protocol.coder;

import io.vertx.core.buffer.Buffer;
import love.ytlsnb.rpc.protocol.AlpacaProtocol;
import love.ytlsnb.rpc.protocol.enums.ProtocolSerializerEnum;
import love.ytlsnb.rpc.serializer.Serializer;
import love.ytlsnb.rpc.serializer.SerializerFactory;

import java.io.IOException;

/**
 * 编码器
 */
public class ProtocolEncoder {
    /**
     * 编码：将协议数据报对象转为Buffer对象
     * @param protocol 协议数据报
     * @return Buffer对象
     */
    public static Buffer encode(AlpacaProtocol<?> protocol) throws IOException {
        if (protocol == null) {
            return Buffer.buffer();
        }
        Buffer buffer = Buffer.buffer();
        AlpacaProtocol.Header header = protocol.getHeader();
        // 往里面加东西，顺序绝对不能错
        buffer.appendByte(header.getMagic())
                .appendByte(header.getVersion())
                .appendByte(header.getSerializer())
                .appendByte(header.getType())
                .appendByte(header.getStatus())
                .appendLong(header.getRequestId());// 数据段长度还没确定，先不添加
        // 拿序列化器
        ProtocolSerializerEnum serializerEnum = ProtocolSerializerEnum.getEnumBySerializer(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化器不存在");
        }
        Serializer serializer = SerializerFactory.getSerializer(serializerEnum.getName());
        // 序列化
        byte[] bodyData = serializer.serialize(protocol.getBody());
        // 将剩下的数据插进去
        buffer.appendInt(bodyData.length)
                .appendBytes(bodyData);
        return buffer;
    }
}
