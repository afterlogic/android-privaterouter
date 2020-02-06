package com.PrivateRouter.PrivateMail.repository;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.PrivateRouter.PrivateMail.model.Message;

import java.util.ArrayList;

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
