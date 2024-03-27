package love.ytlsnb.rpc.protocol.enums;

import lombok.Getter;

/**
 * 协议消息类型枚举
 */
@Getter
public enum ProtocolTypeEnum {
    REQUEST(1),
    RESPONSE(2),
    HEART_BEAT(3),
    OTHERS(0);

    private final int type;

    ProtocolTypeEnum(int type) {
        this.type = type;
    }

    /**
     * 根据消息类型获取枚举
     * @param type 消息类型
     * @return 类型枚举
     */
    public static ProtocolTypeEnum getEnumByType(int type) {
        for (ProtocolTypeEnum value : ProtocolTypeEnum.values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }
}
