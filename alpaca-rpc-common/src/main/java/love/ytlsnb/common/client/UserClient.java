package love.ytlsnb.common.client;

import love.ytlsnb.common.model.User;

/**
 * 用户接口定义，要由提供者实现，由消费者调用
 */
public interface UserClient {
    /**
     * 获取用户
     * @return 用户
     */
    User getUser();
}
