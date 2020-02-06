package com.PrivateRouter.PrivateMail.view.mail_list;

import androidx.paging.PagedListAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.view.utils.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MailListAdapter extends PagedListAdapter<Message, MailViewHolder> {


    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ADD_MORE_BAR = 1;
    private static final int TYPE_EMPTY = 2;

    private boolean selectedMode = false;
    private MailListModeMediator mailListModeMediator;
    private List<Message> selectedMessages = new LinkedList<>();
    private HashMap<Integer, Boolean> expandedMessageUids = new HashMap<>();
    private HashMap<Integer, Boolean> selectedMessageUids = new HashMap<>();
    private boolean flatMode;
    private boolean loading;
    private boolean showMoreBar = false;
    private boolean showMessage = false;
    private boolean isSent = false;

    protected MailListAdapter(DiffUtil.ItemCallback<Message> diffUtilCallback, MailListModeMediator mailListModeMediator, boolean isSend) {
        super(diffUtilCallback);
        this.isSent = isSend;
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
        } else if (viewType == TYPE_ADD_MORE_BAR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mail_list_show_bar, parent, false);
            mailViewHolder = new MailViewBarHolder(view);
        } else if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mail_list_empty, parent, false);
            mailViewHolder = new MailViewHolder(view);
        }
        return mailViewHolder;
    }

    public boolean isMessageSelected(Message message) {
        if (message == null)
            return false;
        else if (selectedMessageUids.get(message.getUid()) == null)
            return false;
        else
            return selectedMessageUids.get(message.getUid());
    }

    public boolean isMessageExpanded(Message message) {
        if (message == null)
            return false;
        else if (expandedMessageUids.get(message.getUid()) == null)
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
            case TYPE_EMPTY:
                bindEmptyBarItem(holder, position);
                break;
        }
    }

    private void bindEmptyBarItem(MailViewHolder holder, int position) {
    }

    private void bindBarItem(MailViewHolder holder, int position) {
        ((MailViewBarHolder) holder).bind(mailListModeMediator);
    }

    private void bindMessageItem(MailViewHolder holder, int position) {

        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        Message message = getItem(position);
        boolean selected = isMessageSelected(message);
        boolean expand = isMessageExpanded(message);
        messageViewHolder.setFlatMode(flatMode);
        messageViewHolder.bind(message, position, selected, expand, this,isSent);
        messageViewHolder.setSelectedMode(selectedMode);

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

        if (selectedMessageUids.get(message.getUid()) != null)
            selectedMessageUids.remove(message.getUid());

        selectedMessageUids.put(message.getUid(), value);

        if (value) {
            if (!selectedMessages.contains(message))
                selectedMessages.add(message);
        } else {
            selectedMessages.remove(message);
        }

        mailListModeMediator.onSelectChange(selectedMessages);
    }

    public void onExpandChange(boolean value, Message message) {

        if (expandedMessageUids.get(message.getUid()) != null)
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

    public boolean isShowEmptyMessage() {
        return showMessage;
    }

    public void setShowEmptyMessage(boolean emptyMessages) {
        this.showMessage = emptyMessages;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }


    public interface OnMessageClick {
        void onMessageClick(Message message, int position);
    }


    @Nullable
    protected Message getItem(int position) {
        if (isShowEmptyMessage() && position == 0)
            return null;
        else if (hasShowMoreBar() && position == getItemCount() - 1)
            return null;
        else if (position < super.getItemCount())
            return super.getItem(position);
        else
            return null;
    }


    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if (isShowEmptyMessage()) {
            count++;
        }
        if (hasShowMoreBar())
            count++;

        return count;
    }


    public void setShowMoreBar(boolean val) {
        Logger.v("bars", "setShowMoreBar=" + val);
        showMoreBar = val;
    }

    public boolean hasShowMoreBar() {

        return showMoreBar;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowEmptyMessage() && position == 0)
            return TYPE_EMPTY;
        if (position == getItemCount() - 1 && hasShowMoreBar())
            return TYPE_ADD_MORE_BAR;
        else
            return TYPE_ITEM;
    }

}