package com.xiaoma.im.vo;

import com.xiaoma.im.entity.MessageSingle;

import java.io.Serializable;

/**
 * @Author Xiaoma
 * @Date 2021/2/16 0016 12:23
 * @Email 1468835254@qq.com
 */
public class MessageSingleVo extends MessageSingle implements Serializable {

    private static final long serialVersionUID = 8050477961361618803L;
    private int commandType;

    private String userAccount;

    private String friendAccount;

    public MessageSingleVo() {
    }

    public MessageSingleVo(int commandType, String userAccount, String friendAccount) {
        this.commandType = commandType;
        this.userAccount = userAccount;
        this.friendAccount = friendAccount;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(String friendAccount) {
        this.friendAccount = friendAccount;
    }
}
