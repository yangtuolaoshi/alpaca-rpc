package love.ytlsnb.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RPCResponse implements Serializable {
    /**
     * 返回数据
     */
    private Object data;

    /**
     * 返回数据的类型
     */
    private Class<?> dataType;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 异常
     */
    private Exception exception;
}
