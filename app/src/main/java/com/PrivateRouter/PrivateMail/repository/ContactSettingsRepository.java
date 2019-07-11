package com.PrivateRouter.PrivateMail.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.ContactSettings;
import com.PrivateRouter.PrivateMail.model.User;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallGetSettings;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class ContactSettingsRepository {
    public static volatile ContactSettingsRepository instance = null;

    @NonNull
    public static ContactSettingsRepository getInstance() {
        ContactSettingsRepository repopsitory = instance;
        if (repopsitory == null) {
            synchronized (ContactSettingsRepository.class) {
                repopsitory = instance;
                if (repopsitory == null) {
                    repopsitory = instance = new ContactSettingsRepository();
                }
            }
        }
        return repopsitory;
    }

    private static final String PREF_NAME = "contact_settings";
    private static final int PREF_MOD = MODE_PRIVATE;

//    public void setContactsPerPage(Context context, int value) {
//        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
//        editor.putInt("ContactsPerPage", value);
//        editor.apply();
//    }
//
//    public int getContactsPerPage(Context context) {
//        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getInt("ContactsPerPage", 20);
//    }
//
//    public void setImportContactsLink(Context context, String value) {
//        setString(context, "ImportContactsLink", value);
//    }
//
//    public String getImportContactsLink(Context context) {
//        return getString(context, "ImportContactsLink", "");
//    }
//
//    public void setStorages(Context context, List<String> list) {
//        setStringSet(context, "Storages", list);
//    }
//
//    public Set<String> getStorages(Context context) {
//        return getStringSet(context, "Storages");
//    }
//
//    public void setImportExportFormats(Context context, List<String> list) {
//        setStringSet(context, "ImportExportFormats", list);
//    }
//
//    public Set<String> getImportExportFormats(Context context) {
//        return getStringSet(context, "ImportExportFormats");
//    }
//
//    private String getString(Context context, String preferenceName, String defaultValue) {
//        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getString(preferenceName, defaultValue);
//    }
//
//    private Set<String> getStringSet(Context context, String preferenceName) {
//        return context.getSharedPreferences(PREF_NAME, PREF_MOD).getStringSet(preferenceName, null);
//    }
//
//    private void setStringSet(Context context, String preferenceName, List<String> value) {
//        Set<String> strings = new HashSet<String>(value);
//        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
//        editor.putStringSet(preferenceName, strings);
//        editor.apply();
//    }
//
//    private void setString(Context context, String preferenceName, String value) {
//        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, PREF_MOD).edit();
//        editor.putString(preferenceName, value);
//        editor.apply();
//    }

    public void getContactSettings(OnContactLoadCallback callback) {
        ContactSettings contactSettings = loadFromCache();
        if(contactSettings == null){
            requestContact(callback);
        } else {
            callback.onContactsLoad(contactSettings);
        }
    }

    private void requestContact(OnContactLoadCallback callback) {
        CallGetSettings callGetSettings = new CallGetSettings(new CallRequestResult<ContactSettings>() {
            @Override
            public void onSuccess(ContactSettings result) {
                saveToCache(result);
                callback.onContactsLoad(result);
            }

            @Override
            public void onFail(ErrorType errorType, int serverCode) {
                callback.onFail(errorType, serverCode);
            }
        });
        callGetSettings.start();
    }

    private void saveToCache(ContactSettings contactSettings) {
        Context context = PrivateMailApplication.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String contactSettingsJson = gson.toJson(contactSettings);

        editor.putString("contact_settings", contactSettingsJson);

        editor.apply();
    }

    private ContactSettings loadFromCache() {
        Context context = PrivateMailApplication.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String contactSettingsJson = sharedPreferences.getString("contact_settings", "");
        if(contactSettingsJson.isEmpty()){
            return null;
        }
        Gson gson = new Gson();
        ContactSettings contactSettings = gson.fromJson(contactSettingsJson, ContactSettings.class);
        return contactSettings;
    }

    public interface OnContactLoadCallback{
        void onContactsLoad(ContactSettings contactSettings);

        void onFail(ErrorType errorType, int serverCode);
    }
}
