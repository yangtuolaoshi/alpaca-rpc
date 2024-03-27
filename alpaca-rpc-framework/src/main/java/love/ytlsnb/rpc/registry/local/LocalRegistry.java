package love.ytlsnb.rpc.registry.local;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务注册器
 */
public class LocalRegistry {
    /**
     * 注册表
     */
    private static final ConcurrentHashMap<String, Class<?>> registry = new ConcurrentHashMap<>();

    /**
     * 注册
     * @param name 服务名称
     * @param clazz 实现类
     */
    public static void register(String name, Class<?> clazz) {
        registry.put(name, clazz);
    }

    /**
     * 注销
     * @param name 服务名称
     */
    public static void withdraw(String name) {
        registry.remove(name);
    }

    /**
     * 获取
     * @param name 服务名称
     * @return 服务实现类
     */
    public static Class<?> get(String name) {
        return registry.get(name);
    }
}
