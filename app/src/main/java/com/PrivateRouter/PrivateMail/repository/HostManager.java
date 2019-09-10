package com.PrivateRouter.PrivateMail.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.PrivateRouter.PrivateMail.BuildConfig;
import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.network.ApiFactory;

public class HostManager {

    public static String getHost() {
        Context context = PrivateMailApplication.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("host", Context.MODE_PRIVATE);
        return sharedPreferences.getString("host", BuildConfig.API_ENDPOINT);
    }

    public static void setHost(String host) {
        ApiFactory.setHost(host);
        ApiFactory.restartService();
        save(PrivateMailApplication.getContext(), host);
    }

    private static void save(Context context, String host ) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("host", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("host", host);
        editor.apply();
    }

    public static boolean init() {
        String host = getHost();
        ApiFactory.setHost(host);
        return true;
    }
}
