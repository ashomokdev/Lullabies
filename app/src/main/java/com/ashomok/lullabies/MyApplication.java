package com.ashomok.lullabies;

import android.app.Application;
import android.content.Context;

/**
 * Created by iuliia on 3/24/17.
 */

public class MyApplication  extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
