package love.ytlsnb.provider;

import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.provider.client.UserClientImpl;
import love.ytlsnb.rpc.RPCApplication;
import love.ytlsnb.rpc.registry.LocalRegistry;
import love.ytlsnb.rpc.server.HttpServer;
import love.ytlsnb.rpc.server.VertXHttpServer;

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
        // 启动Web服务器
        HttpServer httpServer = new VertXHttpServer();
        httpServer.start(6660);
    }
}
