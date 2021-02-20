package com.xiaoma.im.vo;

import com.xiaoma.im.entity.UserInfo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Xiaoma
 * @Date 2021/2/15 0015 22:01
 * @Email 1468835254@qq.com
 */
public class FriendsListVo extends UserInfo implements Serializable {

    private static final long serialVersionUID = -1333651779662337960L;

    private String nickName;

    private Date buildTime;

    public FriendsListVo() {
    }

    public FriendsListVo(String nickName, Date buildTime) {
        this.nickName = nickName;
        this.buildTime = buildTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }
}
