package love.ytlsnb.rpc.config;

import lombok.Data;

/**
 * 注册中心的配置封装
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心名称
     */
    private String registryName;

    /**
     * 注册中心地址
     */
    private String registryAddress;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间
     */
    private Long timeout;
}
