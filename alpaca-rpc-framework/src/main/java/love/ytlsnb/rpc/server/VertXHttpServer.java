package love.ytlsnb.rpc.server;

import io.vertx.core.Vertx;

/**
 * 服务器实现：使用Vert.x实现
 */
public class VertXHttpServer implements HttpServer {
    @Override
    public void start(int port) {
        // 创建服务器对象
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        // 定义处理请求的逻辑
        httpServer.requestHandler(new HttpServerHandler());

        // 监听端口
        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("服务器启动成功！端口为: " + port);
            } else {
                System.out.println("服务器启动失败，请稍后重试！错误信息: " + result.cause());
            }
        });
    }
}
