package com.vid.allvideodownloader;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.vid.allvideodownloader.util.AppLangSessionManager;

import java.util.Locale;

public class MyApplication extends Application {
    AppLangSessionManager appLangSessionManager;
    @Override
    public void onCreate() {
        super.onCreate();
//
//        FirebaseMessaging.getInstance().subscribeToTopic("all");
//        appLangSessionManager = new AppLangSessionManager(getApplicationContext());
//        setLocale(appLangSessionManager.getLanguage());






    }

    public void setLocale(String lang) {
        if (lang.equals("")){
            lang="en";
        }
        Log.d("Support",lang+"");
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


    }




}
