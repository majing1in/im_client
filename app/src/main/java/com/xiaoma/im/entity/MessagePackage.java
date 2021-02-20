package com.xiaoma.im.entity;

import java.io.Serializable;

/**
 * @Author Xiaoma
 * @Date 2021/2/8 0008 12:55
 * @Email 1468835254@qq.com
 */
public class MessagePackage implements Serializable {

    private static final long serialVersionUID = 5040602611581921856L;
    /**
     * 消息长度
     */
    private int length;
    /**
     * 消息类型
     */
    private int type;
    /**
     * 消息体
     */
    private byte[] content;

    public MessagePackage() {
    }

    public MessagePackage(int length, int type, byte[] content) {
        this.length = length;
        this.type = type;
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
