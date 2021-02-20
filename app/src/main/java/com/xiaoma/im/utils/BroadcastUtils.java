package com.xiaoma.im.utils;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BroadcastUtils {

    public static void initLocalBroadcastManager(String action, Context context, String key, int value) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        manager.sendBroadcast(intent);
    }

}
