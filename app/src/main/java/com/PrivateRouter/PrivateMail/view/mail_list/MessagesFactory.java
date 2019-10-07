package com.PrivateRouter.PrivateMail.view.mail_list;

import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.network.logics.LoadMessageLogic;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;

import java.util.ArrayList;
import java.util.List;

import static com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity.EMAIL_SEARCH_PREFIX;

public class MessagesFactory extends AsyncTask<Void, Void, DataSource.Factory<Integer, Message> > {

    private final String filter;
    private final boolean unreadOnly;
    private final ArrayList<Integer> additionalMails;
    private final String currentFolder;
    private boolean useFlatMode = false;

    public OnFactoryCallback getOnEndCallback() {
        return onEndCallback;
    }

    public void setOnEndCallback(OnFactoryCallback onEndCallback) {
        this.onEndCallback = onEndCallback;
    }


    public interface OnFactoryCallback {
        void onFactoryDone(boolean useFlatMode, DataSource.Factory<Integer, Message>  factory);
    }
    private OnFactoryCallback onEndCallback;



    public MessagesFactory(String currentFolder, String filter, boolean unreadOnly, ArrayList<Integer> additionalMails) {
        this.currentFolder = currentFolder;
        this.filter = filter;
        this.unreadOnly = unreadOnly;
        this.additionalMails = additionalMails;
    }

    @Override
    protected DataSource.Factory<Integer, Message>  doInBackground(Void... voids) {
        Log.d(LoadMessageLogic.TAG, "initList currentFolder=" + currentFolder + " filter = " + filter );
        Log.d("bars", "initList currentFolder=" + currentFolder + " filter = " + filter );


        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();


        boolean starredOnly = false;


        String performFolder = currentFolder;
        if (currentFolder.equals( FolderType.VIRTUAL_STARRED_NAME )) {
            Account account = LoggedUserRepository.getInstance().getActiveAccount();
            performFolder = account.getFolders().getFolderName(FolderType.Inbox);
            useFlatMode = true;
            starredOnly = true;
        }

        DataSource.Factory<Integer, Message>  factory;
        if (TextUtils.isEmpty(filter)) {
            factory = database.messageDao().getAllMessagesFactory(performFolder, starredOnly, unreadOnly, additionalMails);
        } else {
            useFlatMode = true;
            if (filter.startsWith(EMAIL_SEARCH_PREFIX) && filter.length() > EMAIL_SEARCH_PREFIX.length()) {
                String value = filter.substring(EMAIL_SEARCH_PREFIX.length() + 1);
                factory = database.messageDao().getAllFilterEmailFactory(performFolder, "%" + value + "%", starredOnly, unreadOnly, additionalMails);
            } else {
                factory = database.messageDao().getAllFilterFactory(performFolder, "%" + filter + "%", starredOnly, unreadOnly, additionalMails);
            }
        }
        return factory;

    }

    @Override
    protected void onPostExecute(DataSource.Factory<Integer, Message>  result) {
        if (onEndCallback!=null)
            onEndCallback.onFactoryDone(useFlatMode, result);
    }
}
