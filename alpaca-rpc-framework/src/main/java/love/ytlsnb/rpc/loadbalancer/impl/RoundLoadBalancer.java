package love.ytlsnb.rpc.loadbalancer.impl;

import love.ytlsnb.rpc.loadbalancer.LoadBalancer;
import love.ytlsnb.rpc.registry.model.ClientMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器
 */
public class RoundLoadBalancer implements LoadBalancer {
    /**
     * 当前的轮询数，用AtomicInteger保证并发一致
     */
    private final AtomicInteger round = new AtomicInteger(0);

    @Override
    public ClientMetaInfo select(Map<String, Object> requestParams, List<ClientMetaInfo> clientMetaInfos) {
        // 无可用服务
        if (clientMetaInfos.isEmpty()) {
            return null;
        }
        // 轮询负载均衡
        int i = round.incrementAndGet();
        return clientMetaInfos.get(i % clientMetaInfos.size());
    }
}
