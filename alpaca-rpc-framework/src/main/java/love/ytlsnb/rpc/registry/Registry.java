package love.ytlsnb.rpc.registry;

import love.ytlsnb.common.model.rpc.ClientMetaInfo;
import love.ytlsnb.rpc.config.RegistryConfig;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 注册中心接口
 */
public interface Registry {
    /**
     * 注册中心初始化
     * @param config 配置
     */
    void init(RegistryConfig config);

    /**
     * 服务注册
     * @param clientMetaInfo 服务信息元信息
     */
    void register(ClientMetaInfo clientMetaInfo) throws ExecutionException, InterruptedException;

    /**
     * 服务下线注销
     * @param clientMetaInfo 服务信息元数据
     */
    void unRegister(ClientMetaInfo clientMetaInfo) throws ExecutionException, InterruptedException;

    /**
     * 服务发现
     * @param clientName 键-服务名称
     */
    List<ClientMetaInfo> find(String clientName) throws ExecutionException, InterruptedException;

    /**
     * 注册中心销毁
     */
    void destroy();
}
