package love.ytlsnb.rpc.protocol.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 协议序列化器枚举
 */
@Getter
public enum ProtocolSerializerEnum {
    JDK(1, "jdk"),
    JSON(2, "json"),
    HESSIAN(3, "hessian");

    private final int serializer;

    private final String name;

    ProtocolSerializerEnum(int serializer, String name) {
        this.serializer = serializer;
        this.name = name;
    }

    /**
     * 根据序列化器获取枚举
     * @param serializer 序列化器
     * @return 枚举
     */
    public static ProtocolSerializerEnum getEnumBySerializer(int serializer) {
        for (ProtocolSerializerEnum value : ProtocolSerializerEnum.values()) {
            if (value.serializer == serializer) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据序列化器名称获取序列化器
     * @param name 序列化器名称
     * @return 序列化器
     */
    public static int getSerializerByName(String name) {
        for (ProtocolSerializerEnum value : ProtocolSerializerEnum.values()) {
            if (Objects.equals(value.name, name)) {
                return value.serializer;
            }
        }
        return -1;
    }
}
