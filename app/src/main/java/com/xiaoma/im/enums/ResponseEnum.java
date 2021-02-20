package com.xiaoma.im.enums;

/**
 * @Author Xiaoma
 * @Date 2021/2/6 0006 23:20
 * @Email 1468835254@qq.com
 */
public enum ResponseEnum {

    RESPONSE_SUCCESS(200,"执行成功"),
    RESPONSE_FAIL(400,"执行失败"),
    RESPONSE_VALID(202,"参数校验失败"),
    RESPONSE_TIMEOUT(204,"过期token无效"),
    RESPONSE_NOT_FIND(205,"该条件没有结果"),
    RESPONSE_UNKNOWN(201,"未知错误");

    private Integer code;
    private String message;

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
