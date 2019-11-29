package com.PrivateRouter.PrivateMail.network.requests;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.Storages;
import com.PrivateRouter.PrivateMail.view.contacts.StoragesCallback;

import java.util.List;


public class CallGetStorage extends AsyncTask<Void, Integer, Boolean> {
    private final StoragesCallback callback;
    private List<Storages> storages;

    public CallGetStorage(@NonNull StoragesCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        storages = PrivateMailApplication.getInstance().getDatabase().storagesDao().getAll();
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        callback.onStorages(storages);
    }


}
