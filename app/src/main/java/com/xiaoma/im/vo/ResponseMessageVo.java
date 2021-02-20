package com.xiaoma.im.vo;

import java.io.Serializable;

/**
 * @Author Xiaoma
 * @Date 2021/2/16 0016 12:29
 * @Email 1468835254@qq.com
 */
public class ResponseMessageVo implements Serializable {
    private static final long serialVersionUID = -2161809327808081932L;
    private String message;
    private String sender;
    private String receiver;

    public ResponseMessageVo() {
    }

    public ResponseMessageVo(String message, String sender, String receiver) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
