package love.ytlsnb.rpc.serializer;

import love.ytlsnb.rpc.spi.SPILoader;

import java.util.HashMap;
import java.util.Map;

import static love.ytlsnb.rpc.constant.SerializerConstant.*;

/**
 * 序列化器工厂（静态工厂）
 */
public class SerializerFactory {
//    /**
//     * 注册表
//     */
//    private static final Map<String, Serializer> serializerMap = new HashMap<String, Serializer>();

    static {
        // 加载SPI
        SPILoader.load(Serializer.class);
    }

    /**
     * 获取序列化器
     * @param name 序列化器名称（KEY）
     * @return 序列化器
     */
    public static Serializer getSerializer(String name) {
//        return serializerMap.get(name);
        return SPILoader.getImplement(Serializer.class, name);
    }
}
