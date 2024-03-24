package love.ytlsnb.provider.client;

import love.ytlsnb.common.client.UserClient;
import love.ytlsnb.common.model.User;

/**
 * 用户服务实现类
 */
public class UserClientImpl implements UserClient {
    @Override
    public User getUser() {
        User user = new User();
        user.setId(1);
        user.setName("张三");
        return user;
    }
}
