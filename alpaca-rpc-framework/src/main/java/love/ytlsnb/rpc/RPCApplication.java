package love.ytlsnb.rpc;

import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.common.model.rpc.ClientMetaInfo;
import love.ytlsnb.rpc.config.RPCConfig;
import love.ytlsnb.rpc.config.RegistryConfig;
import love.ytlsnb.rpc.registry.Registry;
import love.ytlsnb.rpc.registry.RegistryFactory;
import love.ytlsnb.rpc.server.HttpServer;
import love.ytlsnb.rpc.server.VertXHttpServer;
import love.ytlsnb.rpc.utils.ConfigUtils;

import java.util.concurrent.ExecutionException;

import static love.ytlsnb.rpc.constant.RPCConstant.*;

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
     * 注册中心配置
     */
    private static volatile RegistryConfig registryConfig;

    /**
     * RPC框架初始化
     */
    public static void init() {
        // 读取配置文件
        rpcConfig = ConfigUtils.loadConfig(RPCConfig.class, RPC_PREFIX);
        log.info("读取全局配置: {}", rpcConfig.toString());
        registryConfig = ConfigUtils.loadConfig(RegistryConfig.class, RPC_REGISTRY_PREFIX);
        log.info("读取注册中心配置: {}", registryConfig.toString());
        // 初始化注册中心
        String registryName = registryConfig.getRegistryName();
        Registry registry = RegistryFactory.getRegistry(registryName);
        registry.init(registryConfig);
        log.info("注册中心 {} 初始化完毕", registryName);
        // 服务注册
        ClientMetaInfo clientMetaInfo = new ClientMetaInfo();
        clientMetaInfo.setClientName(rpcConfig.getName());
        clientMetaInfo.setClientVersion(DEFAULT_REGISTRY_VERSION);
        String port = rpcConfig.getPort();
        clientMetaInfo.setClientAddress(String.format("%s:%s", rpcConfig.getHost(), port));
        try {
            registry.register(clientMetaInfo);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        // 注册ShutdownHook
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
        // 启动服务器
//        HttpServer httpServer = new VertXHttpServer();
//        httpServer.start(Integer.parseInt(port));
    }

    /**
     * 获取注册中心配置
     * @return 注册中心配置
     */
    public static RegistryConfig getRegistryConfig() {
        if (registryConfig == null) {
            synchronized (RPCApplication.class) {
                if (registryConfig == null) {
                    init();
                }
            }
        }
        return registryConfig;
    }

    /**
     * 获取全局配置
     *
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
