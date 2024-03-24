package love.ytlsnb.rpc.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 */
public interface Serializer {
    /**
     * 序列化
     * @param obj 对象
     * @param <T> 对象的类型
     * @return 序列化后的字节数组
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     * @param bytes 序列化后字节数组
     * @param <T> 对象类型
     * @return 对象
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
