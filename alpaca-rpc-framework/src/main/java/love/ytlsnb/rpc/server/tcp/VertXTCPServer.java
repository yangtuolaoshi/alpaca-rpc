package love.ytlsnb.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.rpc.server.HttpServer;

/**
 * TCP服务器实现
 */
@Slf4j
public class VertXTCPServer implements HttpServer {
    @Override
    public void start(int port) {
        Vertx vertx = Vertx.vertx();
        // 创建TCP服务器
        NetServer netServer = vertx.createNetServer();

        // 请求处理器
        netServer.connectHandler(new TCPServerHandler());

//        netServer.connectHandler(netSocket -> {
//            // 未解决半包和粘包
////            netSocket.handler(buffer -> {
////                System.out.println(buffer);
////            });
//            // 解决半包和粘包
//            // 创建RecordParser对象，指定数据的实际长度
//            RecordParser recordParser = RecordParser.newFixed(48);
//            // 定义数据的处理逻辑
//            recordParser.handler(buffer -> {
//                System.out.println(buffer);
//            });
//            // 为套接字添加一个处理逻辑
//            netSocket.handler(recordParser);
//        });

        // 启动并监听端口
        netServer.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP服务器启动成功，端口为 {}", port);
            } else {
                log.error("TCP服务器启动失败，原因为 {}", result.cause().toString());
            }
        });
    }

    public static void main(String[] args) {
        VertXTCPServer vertXTCPServer = new VertXTCPServer();
        vertXTCPServer.start(6660);
    }
}
