package com.held.utils;

import android.app.Application;
import android.content.Context;


public class HeldApplication extends Application {
    private static final String TAG = HeldApplication.class.getSimpleName();
    public static boolean IS_APP_FOREGROUND;
    private static HeldApplication mInstance;
    private static Context mAppContext;

    public static HeldApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mAppContext = getApplicationContext();
    }

}
