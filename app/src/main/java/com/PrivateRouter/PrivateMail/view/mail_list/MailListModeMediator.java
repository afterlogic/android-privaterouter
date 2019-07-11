package com.PrivateRouter.PrivateMail.view.mail_list;

import android.support.v4.widget.SwipeRefreshLayout;

import com.PrivateRouter.PrivateMail.model.Message;

import java.lang.ref.WeakReference;
import java.util.List;


public class MailListModeMediator {
    boolean selectionMode = false;
    WeakReference<MailListActivity> mailListActivity;
    MailListAdapter mailListAdapter;


    public MailListModeMediator(MailListActivity mailListActivity) {
        this.mailListActivity = new WeakReference<>(mailListActivity);
    }


    public void onSelectModeChange(boolean selectMode ) {
        if (mailListActivity.get()==null)
            return;

        this.selectionMode  = selectMode;

        if (selectMode)
            mailListActivity.get().openSelectMode();
        else
            mailListActivity.get().openNormalMode();
    }

    public void onSelectChange(List<Message> selectedList ) {
        if (mailListActivity.get()==null)
            return;

        mailListActivity.get().onSelectChange(selectedList);
    }


    public boolean isSelectionMode() {
        return selectionMode ;
    }

    public void closeSelectionMode() {
        if (mailListAdapter!=null) {
            mailListAdapter.clearSelections();
            mailListAdapter.setSelectedMode(false);
        }
    }

    public void setAdapter(MailListAdapter mailListAdapter) {
        this.mailListAdapter = mailListAdapter;
    }

    public Integer[] getSelectionList() {

        if (mailListAdapter!=null) {
            List<Message> list = mailListAdapter.getSelectedMessages();
            Integer[] uids = new Integer[list.size()];
            int i = 0;
            for (Message message : list) {
                uids[i] = message.getUid();
                i++;
            }

            return uids;
        }

        return null;
    }

    public void onStartLoadThread() {
        if (mailListActivity!=null && mailListActivity.get()!=null)
            mailListActivity.get().onLoadThread();
    }

    public void onEndLoadThread() {
        if (mailListActivity!=null  && mailListActivity.get()!=null)
            mailListActivity.get().onEndLoadThread();
    }

    public WeakReference<SwipeRefreshLayout> getSwipeRefreshLayout() {
        if (mailListActivity!=null && mailListActivity.get()!=null) {
            return  mailListActivity.get().getSwipeRefreshLayout();
        }

        return null;
    }
}
