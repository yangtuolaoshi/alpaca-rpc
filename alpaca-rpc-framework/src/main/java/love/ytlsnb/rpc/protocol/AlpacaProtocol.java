package love.ytlsnb.rpc.protocol;

import lombok.Data;

/**
 * 自定义协议
 */
@Data
public class AlpacaProtocol<T> {
    /**
     * 协议数据报首部
     */
    @Data
    public static class Header {
        /**
         * 魔数
         */
        private byte magic;

        /**
         * 版本
         */
        private byte version;

        /**
         * 序列化方式
         */
        private byte serializer;

        /**
         * 类型
         */
        private byte type;

        /**
         * 状态码
         */
        private byte status;

        /**
         * id
         */
        private long requestId;

        /**
         * 数据段长度
         */
        private int dataLength;
    }

    /**
     * 数据报首部
     */
    private Header header;

    /**
     * 请求体
     */
    private T body;
}
