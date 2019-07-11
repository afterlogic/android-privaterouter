package com.PrivateRouter.PrivateMail.repository;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MessagesRepository {

    private static final String TAG = "MessagesRepository";

    public void clear(Runnable runnable) {

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
                database.clearAllTables();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                if (runnable!=null)
                    runnable.run();
            }
        } ;
        asyncTask.execute();

    }

    public interface CallbackGetMessages{
        void getMessages(ArrayList<Message> message );
    }
    public static volatile MessagesRepository instance = null;

    @NonNull
    public static MessagesRepository getInstance() {
        MessagesRepository repository = instance;
        if (repository == null) {
            synchronized (MessagesRepository.class) {
                repository = instance;
                if (repository == null) {
                    repository = instance = new MessagesRepository();
                }
            }
        }
        return repository;
    }

    public void update(String folder, ArrayList<Message> collection) {

    }

    public void update(Message message) {

    }

    public void getAllMessages(String folder, CallbackGetMessages callbackGetMessages) {
        /*
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        database.messageDao().getAll().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Message>>() {

                    @Override
                    public void onSuccess(List<Message> messages) {
                        if (callbackGetMessages!=null)
                            callbackGetMessages.getMessages( messages.get(folder));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });


*/

    }
}
