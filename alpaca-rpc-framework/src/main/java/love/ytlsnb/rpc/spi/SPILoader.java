package love.ytlsnb.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化器加载器
 */
@Slf4j
public class SPILoader {
    /**
     * 系统序列化器SPI路径
     */
    private static final String SERIALIZER_SYSTEM_DIR = "META-INF/rpc/system/";

    /**
     * 用户序列化器SPI路径
     */
    private static final String SERIALIZER_USER_DIR = "META-INF/rpc/user/";

    /**
     * 用来存放已加载的SPI对象
     * KEY是接口名称
     * VALUE是这个接口的所有实现类集合
     *
     * 子Map的关系如下
     * KEY是实现类名称
     * VALUE是实现类的类类型
     */
    private static final ConcurrentHashMap<String, Map<String, Class<?>>> loadMap = new ConcurrentHashMap<>();

    /**
     * 根据接口名称加载它所有的实现类
     * @param loadClass 接口的类对象
     * @return 这个接口所有的实现类
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        String interfaceName = loadClass.getSimpleName();
        HashMap<String, Class<?>> implementMap = new HashMap<>();
        // 扫描系统目录和用户目录
        String[] scanDirs = {SERIALIZER_SYSTEM_DIR, SERIALIZER_USER_DIR};
        for (String dir : scanDirs) {
            // 这是这个目录下所有叫这个名字的的资源文件
            URL resource = ResourceUtil.getResource(dir + loadClass.getName());
            if (resource != null) {
                try {
                    // 开启输入流
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    // 读取这个文件里所有的行记录
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length > 1) {// 判断防止不规范的写法
                            Class<?> implement = Class.forName(split[1]);
                            implementMap.put(split[0], implement);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("接口: {} 实现类已加载: {}", loadClass.getName(), implementMap);
        loadMap.put(interfaceName, implementMap);
        return implementMap;
    }

    /**
     * 根据接口名称获取它的所有实现
     * @param loadInterface 要获取实现类的接口
     * @param implementName 实现类名称
     * @return 接口的所有实现类
     */
    public static <T> T getImplement(Class<?> loadInterface, String implementName) {
        String simpleName = loadInterface.getSimpleName();
        // 1. 先拿接口的实现类集合
        Map<String, Class<?>> implementMap = loadMap.get(simpleName);
        if (implementMap == null) {
            throw new RuntimeException("未知的接口类型: " + simpleName);
        }
        // 2. 再拿某个实现类
        Class<?> implementClass = implementMap.get(implementName);
        if (implementClass == null) {
            throw new RuntimeException("未知的接口实现类: " + implementName);
        }
        // 3. 将类实例化
        T implementObj = null;
        try {
            implementObj = (T) implementClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return implementObj;
    }
}
