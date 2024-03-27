package love.ytlsnb.rpc.retryer;

import love.ytlsnb.rpc.model.RPCResponse;
import love.ytlsnb.rpc.retry.Retry;
import love.ytlsnb.rpc.retry.RetryFactory;
import love.ytlsnb.rpc.retry.impl.FixedIntervalRetry;
import love.ytlsnb.rpc.retry.impl.NoRetry;
import org.junit.Test;

/**
 * 重试机制测试
 */
public class RetryTest {
//    private final Retry retry = new NoRetry();
//    private final Retry retry = new FixedIntervalRetry();

    @Test
    public void testRetryer() {
        Retry retry = RetryFactory.getRetry("fixedinterval");
        try {
            RPCResponse rpcResponse = retry.doRetry(() -> {
                System.out.println("我马上就出错");
                int i = 1 / 0;
                return new RPCResponse();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
