package com.PrivateRouter.PrivateMail.network.logics;

import android.os.AsyncTask;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Message;

import java.util.ArrayList;
import java.util.List;

public class AsyncThreadLoader extends  AsyncTask<Void, Void, List<Message>> {
    Message message;
    OnLoadThreadCallback runnable;
    public interface OnLoadThreadCallback {
        void onLoadThread(Message message);
    }

    public AsyncThreadLoader(Message message, OnLoadThreadCallback runnable) {
        this.message = message;
        this.runnable = runnable;

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

    }
    @Override
    protected List<Message> doInBackground(Void... voids) {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        List<Message> messages = database.messageDao().getAllThreadsMessages(message.getFolder(), message.getUid());
        return messages;
    }
    @Override
    protected void onPostExecute(List<Message> result) {
        if (message!=null) {
            message.setThreadList( result );


        }
        else {
        }
        if (runnable!=null)
            runnable.onLoadThread(message);
    }

}