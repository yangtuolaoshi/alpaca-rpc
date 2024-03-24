package love.ytlsnb.consumer;

import love.ytlsnb.common.client.UserClient;
import love.ytlsnb.common.model.User;
import love.ytlsnb.rpc.RPCApplication;
import love.ytlsnb.rpc.proxy.ClientProxyFactory;

/**
 * 消费者启动类
 */
public class ConsumerApplication {
    public static void main(String[] args) {
        // 在这里通过rpc框架拿到实现类
//        UserClient userClient = new UserClientProxy();
//        System.out.println(UserClient.class.getSimpleName());
//        System.out.println(RPCApplication.getRpcConfig());
        // 在这里通过rpc框架拿到实现类
        UserClient userClient = ClientProxyFactory.getProxyObj(UserClient.class);
        User user = userClient.getUser();
        System.out.println(user);
    }
}
