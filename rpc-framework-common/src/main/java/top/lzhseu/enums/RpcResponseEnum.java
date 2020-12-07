package top.lzhseu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzh
 * @date 2020/12/5 19:20
 */
@AllArgsConstructor
@Getter
public enum RpcResponseEnum {

    /**
     * 定义两种 RpcResponse 结果的枚举
     */
    SUCCESS(200, "Success: remote call ok"),
    FAIL(500, "Fail: remote call failed");

    private final int code;

    private final String message;
}
