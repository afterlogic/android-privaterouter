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

public class MailListAdapter extends PagedListAdapter<Message, MailViewHolder> {


    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ADD_MORE_BAR = 1;

    private boolean selectedMode = false;
    private MailListModeMediator mailListModeMediator;
    private List<Message> selectedMessages = new LinkedList<>();
    private HashMap<Integer, Boolean> expandedMessageUids = new HashMap<>();
    private HashMap<Integer, Boolean> selectedMessageUids = new HashMap<>();
    private boolean flatMode;

    protected MailListAdapter(DiffUtil.ItemCallback<Message> diffUtilCallback, MailListModeMediator mailListModeMediator) {
        super(diffUtilCallback);
        this.mailListModeMediator = mailListModeMediator;
        mailListModeMediator.setAdapter(this);
    }


    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MailViewHolder mailViewHolder = null;
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mail_list_main, parent, false);
            MessageViewHolder holder = new MessageViewHolder(view);
            holder.setOnMessageClick(onMessageClick);
            mailViewHolder = holder;
        }
        else if (viewType == TYPE_ADD_MORE_BAR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mail_list_show_bar, parent, false);
            mailViewHolder = new MailViewBarHolder(view);
        }
        return mailViewHolder;
    }

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
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        int type = getItemViewType(position);

        switch (type) {
            case TYPE_ITEM:
                bindMessageItem(holder, position);
                break;
            case TYPE_ADD_MORE_BAR:
                bindBarItem(holder, position);
                break;
        }
    }

    private void bindBarItem(MailViewHolder holder, int position) {
        ((MailViewBarHolder)holder).bind(mailListModeMediator);
    }

    private void bindMessageItem(MailViewHolder holder, int position) {

        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        Message message = getItem(position);
        boolean selected = isMessageSelected(message);
        boolean expand = isMessageExpanded(message);
        messageViewHolder.setFlatMode(flatMode);
        messageViewHolder.bind(message , position, selected, expand, this);
        messageViewHolder.setSelectedMode( selectedMode );

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

    public void useFlatMode() {
        flatMode = true;
    }


    public interface OnMessageClick{
        void onMessageClick(Message message, int position);
    }


    @Nullable
    protected Message getItem(int position) {
        if (hasShowMoreBar() && position == getItemCount()-1 )
            return null;
        else
            return super.getItem(position);
    }


    public  int getItemMessageCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if (hasShowMoreBar())
            count++;

        return count;
    }

    boolean showMoreBar = false;
    public void setShowMoreBar(boolean val) {
        showMoreBar = val;
    }

    public boolean hasShowMoreBar() {

        return showMoreBar;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == getItemCount()-1 && hasShowMoreBar() )
            return TYPE_ADD_MORE_BAR;
        else
            return TYPE_ITEM;
    }

}