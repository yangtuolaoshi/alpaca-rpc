package love.ytlsnb.rpc.retry;

import love.ytlsnb.rpc.model.RPCResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface Retry {
    /**
     * 执行可能需要重试的代码
     * @param callable 一系列操作
     * @return 请求响应结果
     */
    RPCResponse doRetry(Callable<RPCResponse> callable) throws Exception;
}
