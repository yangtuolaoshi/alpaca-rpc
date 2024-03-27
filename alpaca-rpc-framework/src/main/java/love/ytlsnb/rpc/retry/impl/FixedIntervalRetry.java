package love.ytlsnb.rpc.retry.impl;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.rpc.model.RPCResponse;
import love.ytlsnb.rpc.retry.Retry;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定间隔重试
 */
@Slf4j
public class FixedIntervalRetry implements Retry {
    @Override
    public RPCResponse doRetry(Callable<RPCResponse> callable) throws Exception {
        Retryer<RPCResponse> retryer = RetryerBuilder.<RPCResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)// 出现什么异常时重试
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))// 重试的时间间隔
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))// 重试几次就不重试了
                .withRetryListener(new RetryListener() {// 重试时执行的代码
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.error("发生异常，重试次数 {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
