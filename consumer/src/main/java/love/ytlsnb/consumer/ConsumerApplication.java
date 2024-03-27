package love.ytlsnb.consumer;

import love.ytlsnb.common.client.UserClient;
import love.ytlsnb.common.model.User;
import love.ytlsnb.rpc.client.proxy.HTTPClientProxyFactory;
import love.ytlsnb.rpc.client.proxy.TCPClientProxyFactory;

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
//        UserClient userClient = HTTPClientProxyFactory.getProxyObj(UserClient.class);
        UserClient userClient = TCPClientProxyFactory.getProxyObj(UserClient.class);
        for (int i = 0; i < 10; i++) {
            User user = userClient.getUser();
            System.out.println("-----------------------------");
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println(user);
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("-----------------------------");
        }
    }
}
