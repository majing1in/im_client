package com.xiaoma.im.utils;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

public class DataMapUtils {

    private static DataMapUtils dataMapUtils;

    private DataMapUtils(){}

    public static DataMapUtils getInstance() {
        if(ObjectUtil.isEmpty(dataMapUtils)) {
            synchronized (DataMapUtils.class) {
                dataMapUtils = new DataMapUtils();
            }
        }
        return dataMapUtils;
    }

    private Map<String, Object> map = new HashMap<>();

    public void putObj(String key, Object obj) {
        map.put(key, obj);
    }

    public Object getObj(String key) {
        return map.get(key);
    }
}
