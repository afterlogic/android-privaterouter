package com.PrivateRouter.PrivateMail.view.mail_list;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Message;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MailListAdapter extends PagedListAdapter<Message, MessageViewHolder> {


    private boolean selectedMode = false;
    private MailListModeMediator mailListModeMediator;
    private List<Message> selectedMessages = new LinkedList<>();
    private HashMap<Integer, Boolean> expandedMessageUids = new HashMap<>();
    private HashMap<Integer, Boolean> selectedMessageUids = new HashMap<>();

    protected MailListAdapter(DiffUtil.ItemCallback<Message> diffUtilCallback, MailListModeMediator mailListModeMediator) {
        super(diffUtilCallback);
        this.mailListModeMediator = mailListModeMediator;
        mailListModeMediator.setAdapter(this);
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mail_list_main, parent, false);
        MessageViewHolder holder = new MessageViewHolder(view);
        holder.setOnMessageClick(onMessageClick);
        return holder;
    }

     /*
    public void onCurrentListChanged(@Nullable PagedList<Message> currentList) {

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        for (int i =0; i <currentList.size(); i++ ) {
            Message message = currentList.get(i);
            if (message != null && message.getThreadList() != null) {
                List<Message> threadsMessages = database.messageDao().getAllThreadsMessages(message.getFolder(), message.getUid());
                if (threadsMessages == null)
                    threadsMessages = new ArrayList<>();
                message.setThreadList(threadsMessages);
            }
        }
    }*/

    public boolean isMessageSelected(Message message) {
        if (message==null)
            return false;
        else if (selectedMessageUids.get(message.getUid())==null)
            return false;
        else
            return selectedMessageUids.get(message.getUid());
    }

    public boolean isMessageExpanded(Message message) {
        if (message==null)
            return false;
        else if (expandedMessageUids.get(message.getUid())==null)
            return false;
        else
            return expandedMessageUids.get(message.getUid());
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = getItem(position);
        boolean selected = isMessageSelected(message);
        boolean expand = isMessageExpanded(message);
        holder.bind(message , position, selected, expand, this);

        holder.setSelectedMode( selectedMode );

    }
    public Message getMessage(int position) {
        return getItem(position);
    }

    private OnMessageClick onMessageClick;

    public OnMessageClick getOnMessageClick() {
        return onMessageClick;
    }

    public void setOnMessageClick(OnMessageClick onMessageClick) {
        this.onMessageClick = onMessageClick;
    }

    public void onLongTap() {
        selectedMode = !selectedMode;
        notifyDataSetChanged();

        mailListModeMediator.onSelectModeChange(selectedMode);
    }

    public void onSelectChange(boolean value, Message message) {

        if (selectedMessageUids.get(message.getUid())!=null)
            selectedMessageUids.remove(message.getUid());

        selectedMessageUids.put(message.getUid(), value);



        if (value) {
            if (!selectedMessages.contains(message))
                selectedMessages.add(message);
        }
        else {
            selectedMessages.remove(message);
        }

        mailListModeMediator.onSelectChange(selectedMessages);
    }

    public void onExpandChange(boolean value, Message message) {

        if (expandedMessageUids.get(message.getUid())!=null)
            expandedMessageUids.remove(message.getUid());

        expandedMessageUids.put(message.getUid(), value);

        notifyDataSetChanged();
    }

    public boolean isSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode(boolean selectedMode) {
        this.selectedMode = selectedMode;
        mailListModeMediator.onSelectModeChange(selectedMode);
        notifyDataSetChanged();
    }

    public List<Message> getSelectedMessages() {
        return selectedMessages;
    }

    public boolean getSelectedMode() {
        return selectedMode;
    }

    public HashMap<Integer, Boolean> getExpandedMessageUids() {
        return expandedMessageUids;
    }

    public HashMap<Integer, Boolean> getSelectedMessageUids() {
        return selectedMessageUids;
    }

    public MailListModeMediator getMailListModeMediator() {
        return mailListModeMediator;
    }

    public void setMailListModeMediator(MailListModeMediator mailListModeMediator) {
        this.mailListModeMediator = mailListModeMediator;
    }

    public void clearSelections() {
        selectedMessages.clear();
        selectedMessageUids.clear();
    }

    public WeakReference<SwipeRefreshLayout> getSwipeRefreshLayout() {
        return null;//mailListModeMediator.getSwipeRefreshLayout();
    }


    public interface OnMessageClick{
        void onMessageClick(Message message, int position);
    }



}