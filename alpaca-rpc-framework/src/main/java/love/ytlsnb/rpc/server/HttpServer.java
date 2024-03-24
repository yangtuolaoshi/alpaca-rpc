package love.ytlsnb.rpc.server;

/**
 * Web服务器统一接口，方便后续能够实现不同的Web服务器
 */
public interface HttpServer {
    /**
     * 服务器启动
     * @param port 端口号
     */
    void start(int port);
}
