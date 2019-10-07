package com.PrivateRouter.PrivateMail.dbase;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;

import java.util.ArrayList;

public class AsyncDbaseGetMessageCount extends AsyncTask<Void, Void, Long> {
    private final String filter;
    private final boolean unreadOnly;
    private String currentFolder;
    private final ArrayList<Integer> additionalMails;

    public OnEndCallback getOnEndCallback() {
        return onEndCallback;
    }

    public void setOnEndCallback(OnEndCallback onEndCallback) {
        this.onEndCallback = onEndCallback;
    }

    public interface OnEndCallback {
        void onDone(long messageCount);
    }
    private OnEndCallback onEndCallback;


    public AsyncDbaseGetMessageCount(String currentFolder, String filter, boolean unreadOnly, ArrayList<Integer> additionalMails) {
        this.currentFolder = currentFolder;
        this.filter = filter;
        this.unreadOnly = unreadOnly;
        this.additionalMails = additionalMails;
    }
    @Override
    protected Long doInBackground(Void... voids) {

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();

        boolean starredOnly = false;


        String performFolder = currentFolder;
        if (currentFolder.equals( FolderType.VIRTUAL_STARRED_NAME )) {
            Account account = LoggedUserRepository.getInstance().getActiveAccount();
            performFolder = account.getFolders().getFolderName(FolderType.Inbox);
            starredOnly = true;
        }

        long messageCount;
        if (TextUtils.isEmpty(filter)) {
            messageCount = database.messageDao().getAllFactoryCount(performFolder, starredOnly, unreadOnly, additionalMails  );
        }
        else {
            if (filter.startsWith(MailListActivity.EMAIL_SEARCH_PREFIX) && filter.length() > MailListActivity.EMAIL_SEARCH_PREFIX.length()) {
                String value = filter.substring(MailListActivity.EMAIL_SEARCH_PREFIX.length() + 1);
                messageCount = database.messageDao().getAllFilterEmailFactoryCount(performFolder, "%" + value + "%", starredOnly, unreadOnly, additionalMails );
            } else {
                messageCount = database.messageDao().getAllFilterFactoryCount(performFolder, "%" + filter + "%", starredOnly, unreadOnly, additionalMails );
            }
        }
        return  messageCount;
    }

    @Override
    protected void onPostExecute(Long result) {
        if (onEndCallback!=null)
            onEndCallback.onDone(result);
    }

}
