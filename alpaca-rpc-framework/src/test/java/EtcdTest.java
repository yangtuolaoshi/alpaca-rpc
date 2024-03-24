import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EtcdTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建客户端
        Client client = Client.builder().endpoints("http://121.41.90.44:2379").build();

        KV kvClient = client.getKVClient();
//        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence key1 = ByteSequence.from("dir1/hello".getBytes());
        ByteSequence value1 = ByteSequence.from("test_value1".getBytes());

        ByteSequence key2 = ByteSequence.from("dir1/hi".getBytes());
        ByteSequence value2 = ByteSequence.from("test_value2".getBytes());

        // 插入一个键值对
        kvClient.put(key1, value1).get();
        kvClient.put(key2, value2).get();

        ByteSequence prefix = ByteSequence.from("dir1/".getBytes());

        // 根据Key拿到键值对
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        CompletableFuture<GetResponse> getFuture = kvClient.get(prefix, getOption);

        // 从CompletableFuture中拿到值
        GetResponse response = getFuture.get();
        List<KeyValue> kvs = response.getKvs();
        for (KeyValue kv : kvs) {
            System.out.println(kv.getKey() + ": " + kv.getValue());
        }

//        // delete the key
//        kvClient.delete(key).get();
    }
}
