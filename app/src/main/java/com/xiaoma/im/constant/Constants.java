package com.xiaoma.im.constant;

public class Constants {

    public static final int SUCCESS = 1;
    public static final int FAILED = 2;

    public static final int SUCCESS_CONSTANT = 200;
    public static final int FAILED_CONSTANT = 400;


    public static final int PING = 3;

    /**
     * 发送消息
     */
    public static final int SEND = 5;

    /**
     * 接收
     */
    public static final int RECEIVED = 6;

    /**
     * 获取自己的信息
     */
    public static final int ME_INFO = 7;
    /**
     * 修改自己的信息
     */
    public static final int ME_INFO_UPDATE = 8;
    /**
     * 获取好友列表
     */
    public static final int FRIENDS_LIST = 9;

    public static final int FRIEND_LIST_MESSAGE = 10;


    public static final int FRIEND_LIST_APPLY = 37;

    public static final String ACCESS_KEYID = "LTAI4G73XM2LKve5e5ThGku1";
    public static final String ACCESS_KEYID_SECRET = "GRWCcKmm4lFWsqYc28fExWvQEXAHZe";
    public static final String BUCKET_NAME = "server-aliyun-images";
    public static final String END_POINT = "oss-cn-shanghai.aliyuncs.com";

    // https://server-aliyun-images.oss-cn-shanghai.aliyuncs.com/2021-02-15/d5b673929dba4818bcf1102be6d44ab5

    public static String fixPhotoUrl(String s1, String s2) {
        return "https://" + BUCKET_NAME + "." + END_POINT + "/" + s1 + "/" + s2 + ".jpg";
    }
}
