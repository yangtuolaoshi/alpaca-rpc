package love.ytlsnb.rpc.registry;

import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;
import love.ytlsnb.common.model.rpc.ClientMetaInfo;
import love.ytlsnb.rpc.config.RegistryConfig;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 注册中心之Etcd实现
 */
@Slf4j
public class EtcdRegistry implements Registry {
    /**
     * Etcd操作客户端
     */
    private volatile Client client;

    /**
     * Etcd的KV操作客户端
     */
    private volatile KV kvClient;

    @Override
    public void init(RegistryConfig config) {
        String registryAddress = config.getRegistryAddress();
        Long timeout = config.getTimeout();
        this.client = Client.builder()
                .endpoints("http://" + registryAddress)// Etcd的地址
                .connectTimeout(Duration.ofMillis(timeout))// 连接超时时长
                .build();
        this.kvClient = client.getKVClient();
        log.info("注册中心初始化成功");
    }

    @Override
    public void register(ClientMetaInfo clientMetaInfo) throws ExecutionException, InterruptedException {
        Lease leaseClient = client.getLeaseClient();
        // 设置一个超时时间
        long id = leaseClient.grant(30).get().getID();
        // 插入键值对：key是服务名称/服务版本/服务地址
        String registryKey = clientMetaInfo.getKey();
        ByteSequence key = ByteSequence.from(registryKey.getBytes());
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(clientMetaInfo).getBytes());
        // 将超时时间和键值对关联
        PutOption putOption = PutOption.builder().withLeaseId(id).build();
//        kvClient.put(key, value, putOption);
        kvClient.put(key, value);
        log.info("服务 {} 注册成功", registryKey);
    }

    @Override
    public void unRegister(ClientMetaInfo clientMetaInfo) throws ExecutionException, InterruptedException {
        String registryKey = clientMetaInfo.getKey();
        ByteSequence key = ByteSequence.from(registryKey.getBytes());
        kvClient.delete(key).get();
        log.info("服务 {} 已注销", registryKey);
    }

    @Override
    public List<ClientMetaInfo> find(String clientName) throws ExecutionException, InterruptedException {
        log.info("获取键: {}", clientName);
        // 根据键从Etcd中拿到键值对数据
        ByteSequence key = ByteSequence.from((clientName + "/").getBytes());
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        CompletableFuture<GetResponse> futureResponse = kvClient.get(key, getOption);
        GetResponse getResponse = futureResponse.get();
        List<KeyValue> kvs = getResponse.getKvs();
        // 解析拿到的数据
//        LinkedList<ClientMetaInfo> clientMetaInfos = new LinkedList<>();
//        for (KeyValue kv : kvs) {
//            String jsonStr = kv.getValue().toString(StandardCharsets.UTF_8);
//            ClientMetaInfo clientMetaInfo = JSONUtil.toBean(jsonStr, ClientMetaInfo.class);
//            clientMetaInfos.add(clientMetaInfo);
//        }
//        return clientMetaInfos;
        return kvs.stream().map(kv -> {// 类似于js里的数组map操作
            String jsonStr = kv.getValue().toString(StandardCharsets.UTF_8);
            return JSONUtil.toBean(jsonStr, ClientMetaInfo.class);
        }).collect(Collectors.toList());
    }

    @Override
    public void destroy() {
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
        log.info("注册中心已销毁");
    }
}
