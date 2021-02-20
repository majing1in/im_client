package com.xiaoma.im.utils;

import java.util.UUID;

/**
 * @Author Xiaoma
 * @Date 2021/2/7 0007 23:51
 * @Email 1468835254@qq.com
 */
public class UuidUtils {

    public static String getUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

}
