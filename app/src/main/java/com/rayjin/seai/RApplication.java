package com.rayjin.seai;

import android.app.Application;

public class RApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}