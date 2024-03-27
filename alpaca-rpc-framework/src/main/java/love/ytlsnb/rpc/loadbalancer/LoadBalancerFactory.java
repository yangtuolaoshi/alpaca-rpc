package love.ytlsnb.rpc.loadbalancer;

import love.ytlsnb.rpc.spi.SPILoader;

/**
 * 通过SPI拿到负载均衡器
 */
public class LoadBalancerFactory {
    static {
        SPILoader.load(LoadBalancer.class);
    }

    /**
     * 根据名称获取负载均衡器
     * @param name 名称
     * @return 负载均衡器
     */
    public static LoadBalancer getLoadBalancer(String name) {
        return SPILoader.getImplement(LoadBalancer.class, name);
    }
}
