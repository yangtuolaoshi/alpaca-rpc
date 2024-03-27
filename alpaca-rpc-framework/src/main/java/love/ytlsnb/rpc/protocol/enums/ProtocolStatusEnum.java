package love.ytlsnb.rpc.protocol.enums;

import lombok.Getter;

/**
 * 协议状态码枚举
 */
@Getter
public enum ProtocolStatusEnum {
    OK("success", 200),
    BAD_REQUEST("bad request", 400),
    FAILED("failed", 500);

    private final String text;

    private final int code;

    ProtocolStatusEnum(String text, int code) {
        this.text = text;
        this.code = code;
    }

    /**
     * 根据状态码获取枚举
     * @param code 状态码
     * @return 枚举
     */
    public static ProtocolStatusEnum getEnumByCode(int code) {
        for (ProtocolStatusEnum protocolStatusEnum : ProtocolStatusEnum.values()) {
            if (code == protocolStatusEnum.code) {
                return protocolStatusEnum;
            }
        }
        return null;
    }
}
