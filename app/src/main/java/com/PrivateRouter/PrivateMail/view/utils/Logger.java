package com.PrivateRouter.PrivateMail.view.utils;

import android.util.Log;

public class Logger {
    public final static boolean useLog = true;

    public static void v(String tag, String message) {
        if (useLog)
            Log.v(tag, message);
    }

    public  static void d (String tag, String message) {
        if (useLog)
            Log.d(tag, message);
    }

    public static void e(String tag, String message) {
        if (useLog)
            Log.e(tag, message);
    }

    public static void i(String tag, String message) {
        if (useLog)
            Log.i(tag, message);
    }

}
