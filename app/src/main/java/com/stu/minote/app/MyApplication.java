package com.stu.minote.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private static SharedPreferences mSharedPreference;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

    }

    public static MyApplication getApplication() {
        return mInstance;
    }

    public static SharedPreferences getmSharedPreference() {
        if (mSharedPreference == null) {
            mSharedPreference = PreferenceManager
                    .getDefaultSharedPreferences(mInstance);
        }
        return mSharedPreference;
    }

}
