package love.ytlsnb.rpc.loadbalancer.impl;

import love.ytlsnb.rpc.loadbalancer.LoadBalancer;
import love.ytlsnb.rpc.registry.model.ClientMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机负载均衡器
 */
public class RandomLoadBalancer implements LoadBalancer {
    /**
     * 随机数生成器
     */
    private final Random random = new Random();

    @Override
    public ClientMetaInfo select(Map<String, Object> requestParams, List<ClientMetaInfo> clientMetaInfos) {
        // 无服务可用
        if (clientMetaInfos.isEmpty()) {
            return null;
        }
        // 随机选一个
        return clientMetaInfos.get(random.nextInt(clientMetaInfos.size()));
    }
}
