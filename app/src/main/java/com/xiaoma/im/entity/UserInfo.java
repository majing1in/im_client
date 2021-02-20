package com.xiaoma.im.entity;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = -5960082026792569654L;

    private Integer id;

    private String userAccount;

    private String userPassword;

    private String userNickName;

    private String userSign;

    private Integer userGender;

    private Date userBirthday;

    private String userName;

    private String userEmail;

    private String userHeadPhoto;

    private String userSchoolTag;

    private Integer userConstellation;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount == null ? null : userAccount.trim();
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword == null ? null : userPassword.trim();
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName == null ? null : userNickName.trim();
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign == null ? null : userSign.trim();
    }

    public Integer getUserGender() {
        return userGender;
    }

    public void setUserGender(Integer userGender) {
        this.userGender = userGender;
    }

    public Date getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(Date userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail == null ? null : userEmail.trim();
    }

    public String getUserHeadPhoto() {
        return userHeadPhoto;
    }

    public void setUserHeadPhoto(String userHeadPhoto) {
        this.userHeadPhoto = userHeadPhoto == null ? null : userHeadPhoto.trim();
    }

    public String getUserSchoolTag() {
        return userSchoolTag;
    }

    public void setUserSchoolTag(String userSchoolTag) {
        this.userSchoolTag = userSchoolTag == null ? null : userSchoolTag.trim();
    }

    public Integer getUserConstellation() {
        return userConstellation;
    }

    public void setUserConstellation(Integer userConstellation) {
        this.userConstellation = userConstellation;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}