package com.xiaoma.im.enums;

/**
 * @Author Xiaoma
 * @Date 2021/2/8 0008 22:46
 * @Email 1468835254@qq.com
 */
public enum MessageTypeEnum {

    MessageType_STRING(1,"普通字符串"),
    MessageType_IMAGE(2,"图片"),
    MessageType_FILE(3,"文件"),
    MessageType_VIDEO(4,"视频"),
    MessageType_MP3(5,"音频")
    ;

    private Integer code;
    private String message;

    MessageTypeEnum(Integer code, String message) {
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
