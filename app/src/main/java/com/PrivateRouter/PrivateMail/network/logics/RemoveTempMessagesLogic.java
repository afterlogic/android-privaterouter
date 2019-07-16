package com.PrivateRouter.PrivateMail.network.logics;

import android.os.AsyncTask;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;

public class RemoveTempMessagesLogic extends AsyncTask<Void, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        Long [] ids = database.messageDao().getMessageTempIds();
        //database.messageDao().

        database.messageDao().removeTempMessagesIds();

        return true;
    }
}
