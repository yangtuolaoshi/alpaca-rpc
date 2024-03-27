package love.ytlsnb.rpc.loadbalancer;

import love.ytlsnb.rpc.registry.model.ClientMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载
 */
public interface LoadBalancer {
    /**
     * 负载均衡选择
     * @param requestParams 请求参数
     * @param clientMetaInfos 服务列表
     * @return 选定的服务
     */
    ClientMetaInfo select(Map<String, Object> requestParams, List<ClientMetaInfo> clientMetaInfos);
}
