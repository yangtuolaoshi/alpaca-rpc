package love.ytlsnb.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import love.ytlsnb.common.client.UserClient;
import love.ytlsnb.common.model.User;
import love.ytlsnb.rpc.model.RPCRequest;
import love.ytlsnb.rpc.model.RPCResponse;
import love.ytlsnb.rpc.serializer.impl.JDKSerializer;
import love.ytlsnb.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理：用户服务的代理对象
 */
public class UserClientProxy implements UserClient {
    @Override
    public User getUser() {
        // 构造请求对象
        RPCRequest rpcRequest = RPCRequest.builder()
                .clientName("UserClient")
                .methodName("getUser")
                .build();
        try {
            // 对请求对象序列化
            Serializer serializer = new JDKSerializer();
            byte[] reqBytes = serializer.serialize(rpcRequest);
            // 发送请求
            HttpResponse resp = HttpRequest
                    .post("http://localhost:6660")
                    .body(reqBytes)
                    .execute();
            // 处理响应，将响应反序列化
            byte[] respBody = resp.bodyBytes();
            RPCResponse rpcResponse = serializer.deserialize(respBody, RPCResponse.class);
            // 返回结果
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
