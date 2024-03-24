package love.ytlsnb.rpc.registry;

import love.ytlsnb.rpc.spi.SPILoader;

/**
 * 注册中心工厂
 */
public class RegistryFactory {
    static {
        SPILoader.load(Registry.class);
    }

    /**
     * 获取指定注册中心
     * @param name 注册中心名称
     * @return 注册中心对象
     */
    public static Registry getRegistry(String name) {
        return SPILoader.getImplement(Registry.class, name);
    }
}
