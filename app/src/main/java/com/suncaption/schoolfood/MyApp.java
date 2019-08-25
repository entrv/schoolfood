package com.suncaption.schoolfood;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

public class MyApp extends Application {
     private static MyApp _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        JodaTimeAndroid.init(this);
    }
    public static MyApp getInstance(){
        return _instance;
    }
}
