package love.ytlsnb.rpc.retry.impl;

import love.ytlsnb.rpc.model.RPCResponse;
import love.ytlsnb.rpc.retry.Retry;

import java.util.concurrent.Callable;

/**
 * 不重试策略
 */
public class NoRetry implements Retry {
    @Override
    public RPCResponse doRetry(Callable<RPCResponse> callable) throws Exception {
        return callable.call();
    }
}
