package com.PrivateRouter.PrivateMail.repository;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class SettingsRepository {
    public static volatile SettingsRepository instance = null;

    @NonNull
    public static SettingsRepository getInstance() {
        SettingsRepository repository = instance;
        if (repository == null) {
            synchronized (SettingsRepository.class) {
                repository = instance;
                if (repository == null) {
                    repository = instance = new SettingsRepository();
                }
            }
        }
        return repository;
    }


    private static final String PREF_NAME = "prefence";
    private static final int PREF_MOD = MODE_PRIVATE;


    private String getString(Context context, String preferenceName, String defaultValue) {
        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getString(preferenceName, defaultValue);
    }


    private Set<String> getStringSet(Context context, String preferenceName) {
        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getStringSet(preferenceName, null);
    }

    private void setStringSet(Context context, String preferenceName, List<String> value) {
        Set<String> strings = new HashSet<String>(value);
        SharedPreferences.Editor editor =  context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
        editor.putStringSet(preferenceName, strings);
        editor.apply();
    }

    private void setString(Context context, String preferenceName, String value) {
        SharedPreferences.Editor editor =  context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
        editor.putString(preferenceName, value);
        editor.apply();
    }


    public boolean isTime24Format(Context context) {
        String str = getString(context, "timeFormat" , "");

        return str.isEmpty() || str.equals("24_time_format");
    }

    public void setTimeFormat(Context context, boolean pmTimeFormat) {
        String val = pmTimeFormat? "pm_time_format": "24_time_format";
        setString(context, "timeFormat", val );
    }

    public int getSyncFrequency(Context context) {
        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getInt("SyncFrequency", 0);
    }

    public void setSyncFrequency(Context context, int value) {
        SharedPreferences.Editor editor =  context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
        editor.putInt("SyncFrequency", value);
        editor.apply();
    }

    public int getSyncPeriod(Context context) {
        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getInt("SyncPeriod", 0);
    }

    public void setSyncPeriod(Context context, int value) {
        SharedPreferences.Editor editor =  context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
        editor.putInt("SyncPeriod", value);
        editor.apply();
    }

    public long getLastSyncDate(Context context) {
        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getLong("LastSyncDate", 0);
    }


    public void setLastSyncDate(Context context, long value) {
        SharedPreferences.Editor editor =  context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
        editor.putLong("LastSyncDate", value);
        editor.apply();
    }


    public boolean isNightMode(Context context) {
        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getBoolean("NightMode", false);
    }

    public void setNightMode(Context context, boolean nightMode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
        editor.putBoolean("NightMode", nightMode);
        editor.apply();
    }
}
