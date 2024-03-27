package love.ytlsnb.provider;

import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.provider.client.UserClientImpl;
import love.ytlsnb.rpc.RPCApplication;
import love.ytlsnb.rpc.config.RPCConfig;
import love.ytlsnb.rpc.registry.Registry;
import love.ytlsnb.rpc.registry.RegistryFactory;
import love.ytlsnb.rpc.registry.local.LocalRegistry;
import love.ytlsnb.rpc.registry.model.ClientMetaInfo;
import love.ytlsnb.rpc.server.tcp.VertXTCPServer;

import java.util.concurrent.ExecutionException;

import static love.ytlsnb.rpc.constant.RPCConstant.DEFAULT_REGISTRY_VERSION;

/**
 * 服务提供者启动类
 */
@Slf4j
public class ProviderApplication {
    public static void main(String[] args) {
        // RPC框架初始化
        RPCApplication.init();
        // 方法注册
        LocalRegistry.register("UserClient", UserClientImpl.class);
        // 服务注册
        RPCConfig rpcConfig = RPCApplication.getRpcConfig();
        ClientMetaInfo clientMetaInfo = new ClientMetaInfo();
        clientMetaInfo.setClientName(rpcConfig.getName());
        clientMetaInfo.setClientVersion(DEFAULT_REGISTRY_VERSION);
        String port = rpcConfig.getPort();
        clientMetaInfo.setClientAddress(String.format("%s:%s", rpcConfig.getHost(), port));
        Registry registry = RegistryFactory.getRegistry(RPCApplication.getRegistryConfig().getRegistryName());
        try {
            registry.register(clientMetaInfo);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        // 启动Web服务器
        VertXTCPServer tcpServer = new VertXTCPServer();
        tcpServer.start(6660);
//        HttpServer httpServer = new VertXHttpServer();
//        httpServer.start(6660);
    }
}
