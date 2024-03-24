package love.ytlsnb.rpc.utils;

import cn.hutool.setting.dialect.Props;

/**
 * 配置文件工具类
 */
public class ConfigUtils {
    /**
     * 加载配置（指定环境）
     * @param configClass 配置类类对象
     * @param prefix 配置前缀
     * @param env 环境dev sit prod等
     * @param <T> 配置类类型
     * @return 配置类对象
     */
    public static <T> T loadConfig(Class<T> configClass, String prefix, String env) {
        // 获取配置文件名称（允许多环境下不同的配置文件名称）
        StringBuilder sb = new StringBuilder("application");
        if (env != null && !"".equals(env)) {
            sb.append("-").append(env);
        }
        sb.append(".properties");
        String configName = sb.toString();
        // 获取配置对象并返回
        Props props = new Props(configName);// 参数表示配置文件的路径
        return props.toBean(configClass, prefix);// toBean()方法用来将读取到的配置封装到指定对象中
    }

    /**
     * 加载配置（不指定环境）
     * @param configClass 配置类类对象
     * @param prefix 配置前缀，比如rpc.name中rpc就是prefix
     * @param <T> 配置类类型
     * @return 配置类对象
     */
    public static <T> T loadConfig(Class<T> configClass, String prefix) {
        return loadConfig(configClass, prefix, "");
    }
}
