package love.ytlsnb.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 请求模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RPCRequest implements Serializable {
    /**
     * 服务名称
     */
    private String clientName;

    /**
     * 版本号
     */
    private String clientVersion;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 参数列表
     */
    private Object[] params;
}
