package love.ytlsnb.rpc.loadbalancer.impl;

import love.ytlsnb.rpc.loadbalancer.LoadBalancer;
import love.ytlsnb.rpc.registry.model.ClientMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {
    /**
     * TreeMap可以实现一致性哈希环
     */
    private final TreeMap<Integer, ClientMetaInfo> nodes = new TreeMap<>();

    /**
     * 虚拟结点数量
     */
    public static final int NODE_NUM = 100;

    @Override
    public ClientMetaInfo select(Map<String, Object> requestParams, List<ClientMetaInfo> clientMetaInfos) {
        // 无服务可用
        if (clientMetaInfos.isEmpty()) {
            return null;
        }
        // 构建虚拟结点环
        for (ClientMetaInfo clientMetaInfo : clientMetaInfos) {
            for (int i = 0; i < NODE_NUM; i++) {
                int hash = (clientMetaInfo.getClientAddress() + "#" + i).hashCode();
                nodes.put(hash, clientMetaInfo);
            }
        }
        // 获取请求的哈希值
        int hash = requestParams.hashCode();
        // 从环中找到邻近的结点返回
        // ceilingEntry() 方法用于查找大于或等于指定键的最小键值对
        Map.Entry<Integer, ClientMetaInfo> entry = nodes.ceilingEntry(hash);
        if (entry == null) {// 没有更大的了，就返回头结点
            return nodes.firstEntry().getValue();
        } else {// 返回它邻近的下一个结点
            return entry.getValue();
        }
    }
}
