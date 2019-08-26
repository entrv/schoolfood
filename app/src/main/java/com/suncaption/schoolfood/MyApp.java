package com.suncaption.schoolfood;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;

public class MyApp extends Application {
     private static MyApp _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        JodaTimeAndroid.init(this);
        DateTimeZone.setDefault(DateTimeZone.forID("Asia/Seoul"));
    }
    public static MyApp getInstance(){
        return _instance;
    }
}
