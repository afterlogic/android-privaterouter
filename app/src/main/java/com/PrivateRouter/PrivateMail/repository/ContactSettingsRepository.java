package com.PrivateRouter.PrivateMail.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.ContactSettings;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallGetSettings;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.google.gson.Gson;

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

    public void getContactSettings(OnContactLoadCallback callback) {
        ContactSettings contactSettings = loadFromCache();
        if (contactSettings == null) {
            requestContactSettings(callback);
        } else {
            callback.onContactsLoad(contactSettings);
        }
    }

    private void requestContactSettings(OnContactLoadCallback callback) {
        CallGetSettings callGetSettings = new CallGetSettings(new CallRequestResult<ContactSettings>() {
            @Override
            public void onSuccess(ContactSettings result) {
                saveToCache(result);
                callback.onContactsLoad(result);
            }

            @Override
            public void onFail(ErrorType errorType, String errorString,  int serverCode) {
                callback.onFail(errorType,errorString, serverCode);
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
        if (contactSettingsJson.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();
        ContactSettings contactSettings = gson.fromJson(contactSettingsJson, ContactSettings.class);
        return contactSettings;
    }

    public interface OnContactLoadCallback {
        void onContactsLoad(ContactSettings contactSettings);

        void onFail(ErrorType errorType, String errorString,  int serverCode);
    }
}
