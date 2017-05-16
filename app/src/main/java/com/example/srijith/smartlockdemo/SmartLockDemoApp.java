package com.example.srijith.smartlockdemo;

import android.app.Application;

import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by Srijith on 07-05-2017.
 */

public class SmartLockDemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
