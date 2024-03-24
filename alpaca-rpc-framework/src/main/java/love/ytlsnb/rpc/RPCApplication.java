package love.ytlsnb.rpc;

import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.rpc.config.RPCConfig;
import love.ytlsnb.rpc.utils.ConfigUtils;

import static love.ytlsnb.rpc.constant.RPCConstant.RPC_PREFIX;

/**
 * RPC框架启动类
 */
@Slf4j
public class RPCApplication {
    /**
     * 全局配置
     */
    private static volatile RPCConfig rpcConfig;

    /**
     * RPC框架初始化
     */
    public static void init() {
        RPCApplication.rpcConfig = ConfigUtils.loadConfig(RPCConfig.class, RPC_PREFIX);
        log.info("读取配置文件: {}", rpcConfig.toString());
    }

    /**
     * 获取全局配置
     * @return 全局配置
     */
    public static RPCConfig getRpcConfig() {
        // 双重检查锁实现单例模式
        if (RPCApplication.rpcConfig == null) {
            synchronized (RPCApplication.class) {
                if (RPCApplication.rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
