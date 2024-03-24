package love.ytlsnb.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import love.ytlsnb.common.model.rpc.RPCRequest;
import love.ytlsnb.common.model.rpc.RPCResponse;
import love.ytlsnb.rpc.RPCApplication;
import love.ytlsnb.rpc.config.RPCConfig;
import love.ytlsnb.rpc.serializer.Serializer;
import love.ytlsnb.rpc.serializer.SerializerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理工厂（静态工厂）
 */
public class ClientProxyFactory {
    /**
     * 静态工厂获取代理对象
     * @param clientClass 服务类的类型（接口）
     * @param <T> 服务的类型
     * @return 代理对象
     */
    public static <T> T getProxyObj(Class<T> clientClass) {
        return (T) Proxy.newProxyInstance(
                clientClass.getClassLoader(),
                new Class[]{clientClass},
                (Object proxy, Method method, Object[] args) -> {
                    // 在这里发送请求，调用目标对象
                    // 1. 构造请求对象并序列化
                    // 通过配置文件获取指定序列化器
                    RPCConfig rpcConfig = RPCApplication.getRpcConfig();
                    String serializerName = rpcConfig.getSerializer();
                    Serializer serializer = SerializerFactory.getSerializer(serializerName);
                    RPCRequest rpcRequest = RPCRequest.builder()
                            .clientName(method.getDeclaringClass().getSimpleName())
                            .methodName(method.getName())
                            .params(args)
                            .paramTypes(method.getParameterTypes())
                            .build();
                    byte[] reqBytes = serializer.serialize(rpcRequest);
                    // 2. 发送请求
                    HttpResponse resp = HttpRequest.post("http://localhost:6660")
                            .body(reqBytes)
                            .execute();
                    // 3. 解析响应
                    byte[] respBytes = resp.bodyBytes();
                    RPCResponse rpcResponse = serializer.deserialize(respBytes, RPCResponse.class);
                    // 4. 返回数据
                    return rpcResponse.getData();
                }
        );
    }
}
