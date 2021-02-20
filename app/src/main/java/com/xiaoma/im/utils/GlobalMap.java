package com.xiaoma.im.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalMap {

    private static Map<String, String> map = new ConcurrentHashMap<String, String>();

    public static void put(String key, String value) {
        map.put(key, value);
    }

    public static String get(String key) {
        return map.get(key);
    }
}
