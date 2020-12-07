package top.lzhseu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzh
 * @date 2020/12/5 19:07
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    /**
     * gzip 类型
     */
    GZIP((byte) 1, "gzip");

    private final byte code;

    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum type : CompressTypeEnum.values()) {
            if (type.getCode() == code) {
                return type.getName();
            }
        }
        return null;
    }
}
