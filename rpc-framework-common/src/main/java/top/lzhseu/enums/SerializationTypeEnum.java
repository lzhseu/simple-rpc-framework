package top.lzhseu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzh
 * @date 2020/12/5 19:12
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {

    /**
     * JDK: 使用 JDK 自带的序列化
     * KYRO: 使用 kryo 序列化
     * PROTOSTUFF: 使用 protoBuff 序列化
     */
    JDK((byte) 1, "jdk"),
    KRYO((byte) 2, "kryo"),
    PROTOSTUFF((byte) 3, "protobuff");

    private final byte code;

    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum type : SerializationTypeEnum.values()) {
            if (type.getCode() == code) {
                return type.getName();
            }
        }
        return null;
    }
}
