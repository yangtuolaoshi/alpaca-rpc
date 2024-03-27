package love.ytlsnb.rpc.registry.model;

import lombok.Data;

/**
 * 服务注册信息元数据描述
 */
@Data
public class ClientMetaInfo {
    /**
     * 服务名称
     */
    private String clientName;

    /**
     * 服务版本
     */
    private String clientVersion;

    /**
     * 访问地址
     */
    private String clientAddress;

    /**
     * 获取键名称
     * @return 键名称 /服务名称/版本/服务结点地址
     */
    public String getKey() {
        return String.format("%s:%s/%s", clientName, clientVersion, clientAddress);
    }
}
