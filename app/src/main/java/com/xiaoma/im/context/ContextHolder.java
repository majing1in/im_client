package com.xiaoma.im.context;

import android.content.Context;

public class ContextHolder {
    static Context ApplicationContext;

    public static void initial(Context context) {
        ApplicationContext = context;
    }

    public static Context getContext() {
        return ApplicationContext;
    }
}

