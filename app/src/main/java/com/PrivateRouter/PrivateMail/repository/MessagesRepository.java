package com.PrivateRouter.PrivateMail.repository;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListAdapter;
import com.PrivateRouter.PrivateMail.view.mail_list.MessageDiffUtilCallback;

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

    private LiveData<PagedList<Message>> liveData;

    public void updateMessageList(LifecycleOwner lifecycleOwner, DataSource.Factory<Integer, Message> factory) {

        int messagePerMessage = 10;
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(messagePerMessage)
                .setInitialLoadSizeHint(messagePerMessage*3)
                .setPrefetchDistance(messagePerMessage)
                .build();


        if (liveData!=null)
            liveData.removeObservers(lifecycleOwner);

        liveData = new LivePagedListBuilder<>(factory, config)
                .build();


        liveData.observe(lifecycleOwner, new Observer<PagedList<Message>>() {
            @Override
            public void onChanged(@Nullable PagedList<Message> messages) {
                for (OnUpdateCallback callback : callbacks) {
                    callback.onListUpdated(messages);




                }
            }
        });
    }

    public interface OnUpdateCallback {
        void onListUpdated(PagedList<Message> messagePagedList);
    }

    private ArrayList<OnUpdateCallback> callbacks = new ArrayList<>();
    public void addCallback(OnUpdateCallback callback) {
        callbacks.add( callback );
    }

    public int getSize() {
        if (liveData == null || liveData.getValue() == null)
            return 0;
        else
            return liveData.getValue().size();
    }

    public Message getMessage(int index) {
        if (liveData == null || liveData.getValue() == null || index >= getSize())
            return  null;
        else
            return liveData.getValue().get(index);
    }



}
