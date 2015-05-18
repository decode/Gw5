package edu.guet.jjhome.guetw5;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import net.danlew.android.joda.JodaTimeAndroid;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        ActiveAndroid.initialize(this);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
