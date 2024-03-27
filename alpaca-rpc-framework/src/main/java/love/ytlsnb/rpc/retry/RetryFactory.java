package love.ytlsnb.rpc.retry;

import love.ytlsnb.rpc.spi.SPILoader;

/**
 * 重试机制工厂
 */
public class RetryFactory {
    static {
        SPILoader.load(Retry.class);
    }

    /**
     * 获取重试机制
     * @param name 重试机制名称
     * @return 重试机制对象
     */
    public static Retry getRetry(String name) {
        return SPILoader.getImplement(Retry.class, name);
    }
}
