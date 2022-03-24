package com.rayjin.seai;

import android.app.Application;

import com.rayjin.seai.Classifier.Classifier;

public class RApplication extends Application
{
    public static boolean Rflag=true;
    public static Classifier RClassifier;
    @Override
    public void onCreate() {
        super.onCreate();
        RClassifier =  new Classifier();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}