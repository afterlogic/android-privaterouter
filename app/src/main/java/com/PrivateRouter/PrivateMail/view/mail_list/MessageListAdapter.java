package com.PrivateRouter.PrivateMail.view.mail_list;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Message;

import java.util.LinkedList;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter< MessageViewHolder> {


    private List<Message> mailList = new LinkedList<>();
    private MailListAdapter mailListAdapter;

    public MessageListAdapter(List<Message> mailList, MailListAdapter mailListAdapter) {
        super();
        this.mailList = mailList;
        this.mailListAdapter = mailListAdapter;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mail_list_main, parent, false);
        MessageViewHolder holder = new MessageViewHolder(view);
        holder.setOnMessageClick( mailListAdapter.getOnMessageClick() );
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = getItem(position);
        boolean selected = mailListAdapter.isMessageSelected(message);
        boolean expand = mailListAdapter.isMessageExpanded(message);

        holder.bind(message , position, selected, expand, mailListAdapter);

        holder.setSelectedMode( mailListAdapter.getSelectedMode() );

    }

    @Override
    public int getItemCount() {
        if (mailList==null)
            return 0;
        else return  mailList.size();
    }


    private Message getItem(int position) {
        return mailList.get(position);
    }





}