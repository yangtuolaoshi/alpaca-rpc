package love.ytlsnb.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

/**
 * TCP协议客户端
 */
@Slf4j
public class VertXTCPClient {
    public void start(int port, String host) {
        Vertx vertx = Vertx.vertx();
        // 这个方法用于创建一个TCP客户端，并且创建连接
        vertx.createNetClient().connect(port, host, result -> {
            if (result.succeeded()) {
                log.info("连接成功！服务器地址 {}:{}", host, port);
                // 创建一个套接字
                NetSocket socket = result.result();
                // 写一点儿数据发送给服务器
                for (int i = 0; i < 1000; i++) {
                    socket.write("Hello!Hello!Hello!Hello!Hello!Hello!Hello!Hello!");
                }
                // 接收响应
                socket.handler(buffer -> {
                    log.info("接收的数据是 {}", buffer.toString());
                });
            } else {
                log.info("连接失败！原因为 {}", result.cause().toString());
            }
        });
    }

    public static void main(String[] args) {
        VertXTCPClient vertXTCPClient = new VertXTCPClient();
        vertXTCPClient.start(6660, "localhost");
    }
}
