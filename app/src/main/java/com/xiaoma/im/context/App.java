package com.xiaoma.im.context;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.initial(this);
    }
}