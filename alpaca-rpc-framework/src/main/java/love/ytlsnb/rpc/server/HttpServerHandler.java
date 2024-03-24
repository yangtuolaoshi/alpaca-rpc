package love.ytlsnb.rpc.server;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import love.ytlsnb.common.model.rpc.RPCRequest;
import love.ytlsnb.common.model.rpc.RPCResponse;
import love.ytlsnb.rpc.RPCApplication;
import love.ytlsnb.rpc.config.RPCConfig;
import love.ytlsnb.rpc.registry.LocalRegistry;
import love.ytlsnb.rpc.serializer.JDKSerializer;
import love.ytlsnb.rpc.serializer.Serializer;
import love.ytlsnb.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 请求处理器
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        System.out.println("接收了一个请求: " + request.method() + "-" + request.uri());

        request.bodyHandler(body -> {
            RPCResponse rpcResponse = new RPCResponse();
            // 通过配置文件获取指定序列化器
            RPCConfig rpcConfig = RPCApplication.getRpcConfig();
            String serializerName = rpcConfig.getSerializer();
            Serializer serializer = SerializerFactory.getSerializer(serializerName);
            try {
                // 1. 反序列化，读取数据
                byte[] bytes = body.getBytes();
                RPCRequest rpcRequest;
                rpcRequest = serializer.deserialize(bytes, RPCRequest.class);
                // 2. 获取请求数据，解析出要调用的实现类和方法
                // 2.1 获取要调用的服务
                String clientName = rpcRequest.getClientName();
                Class<?> clazz = LocalRegistry.get(clientName);
                // 2.2 获取要调用的方法
                String methodName = rpcRequest.getMethodName();
                Method method= clazz.getMethod(methodName);;
                // 3. 调用方法
                Object obj = clazz.newInstance();
                Object[] params = rpcRequest.getParams();
                Object rtnData = method.invoke(obj, params);
                // 4. 返回数据
                rpcResponse.setData(rtnData);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("success");
                doResponse(request, rpcResponse, serializer);
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());
                doResponse(request, rpcResponse, serializer);
            }
        });
    }

    /**
     * 响应数据
     * @param request 实际发出的请求（不是RPC请求数据）
     * @param rpcResponse 要响应的数据
     * @param serializer 序列化器
     */
    private void doResponse(HttpServerRequest request, RPCResponse rpcResponse, Serializer serializer) {
        // 构造响应数据
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] bytes = serializer.serialize(rpcResponse);
            // 响应
            httpServerResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer("出错了，请稍后重试..."));
        }
    }
}
