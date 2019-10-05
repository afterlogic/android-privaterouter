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
}
