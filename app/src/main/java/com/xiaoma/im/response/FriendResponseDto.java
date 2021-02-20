package com.xiaoma.im.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Xiaoma
 * @Date 2021/2/10 0010 15:41
 * @Email 1468835254@qq.com
 */

public class FriendResponseDto implements Serializable {

    private static final long serialVersionUID = -5799960382876871630L;

    private String friendAccount;
    private String friendNickName;
    private Integer friendId;
    private String photo;
    private Date time;

    public FriendResponseDto() {
    }

    public FriendResponseDto(String friendAccount, String friendNickName, Integer friendId, String photo, Date time) {
        this.friendAccount = friendAccount;
        this.friendNickName = friendNickName;
        this.friendId = friendId;
        this.photo = photo;
        this.time = time;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(String friendAccount) {
        this.friendAccount = friendAccount;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
