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
}
