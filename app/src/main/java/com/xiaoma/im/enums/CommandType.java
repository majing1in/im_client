package com.xiaoma.im.enums;

/**
 * @Author Xiaoma
 * @Date 2021/2/8 0008 22:42
 * @Email 1468835254@qq.com
 */
public enum CommandType {
    COMMAND_LOGIN(1,"登录"),
    COMMAND_REGISTER(2,"注册"),
    COMMAND_UNLINE(3,"注销"),
    COMMAND_HEATBEAT(4,"心跳包"),
    COMMAND_SEND(5,"发送消息"),
    COMMAND_RECEIVE(6,"接收消息"),
    COMMAND_APPLY(7,"好友申请"),
    COMMAND_MESSAGE(8,"未读消息")
    ;

    private Integer code;
    private String message;

    CommandType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
