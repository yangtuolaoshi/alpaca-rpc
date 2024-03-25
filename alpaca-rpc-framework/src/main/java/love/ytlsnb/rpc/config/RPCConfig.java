package love.ytlsnb.rpc.config;

import lombok.Data;

/**
 * 保存RPC配置信息
 */
@Data
public class RPCConfig {
    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务版本
     */
    private String version;

    /**
     * 主机名称
     */
    private String host;

    /**
     * 端口号
     */
    private String port;

    /**
     * 序列化器
     */
    private String serializer;

    /**
     * 获取服务的名称
     * @return 服务名称:版本
     */
    public String getClientName() {
        return String.format("%s:%s", name, version);
    }

    /**
     * 获取访问地址
     * @return 主机:端口
     */
    public String getAddress() {
        return String.format("%s:%s", host, port);
    }
}
