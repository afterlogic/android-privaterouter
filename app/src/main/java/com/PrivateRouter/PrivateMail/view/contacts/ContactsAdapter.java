package com.PrivateRouter.PrivateMail.view.contacts;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ContactsAdapter extends PagedListAdapter<Contact, ContactViewHolder> {

    ContactsListModeMediator contactsListModeMediator;

    protected ContactsAdapter(DiffUtil.ItemCallback<Contact> diffUtilCallback, ContactsListModeMediator contactsListModeMediator) {
        super(diffUtilCallback);
        this.contactsListModeMediator = contactsListModeMediator;
        contactsListModeMediator.setAdapter(this);
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        ContactViewHolder holder = new ContactViewHolder(view);
        holder.setOnContactClick(onContactClick);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        Contact contact = getItem(position);
        holder.bind(contact , position, this, contactsListModeMediator.isSelectionMode(), contactsListModeMediator.isContactSelected(contact));
     }
    public Contact getContact(int position) {
        return getItem(position);
    }

    private OnContactClick onContactClick;


    public void onSelectChange(boolean value, Contact contact) {
        contactsListModeMediator.onSelectChange(value, contact);
    }




    public OnContactClick getOnContactClick() {
        return onContactClick;
    }

    public void setOnContactClick(OnContactClick onContactClick) {
        this.onContactClick = onContactClick;
    }

    public void onLongTap() {
        if(!contactsListModeMediator.isSelectionMode()) {
            contactsListModeMediator.setSelectionMode(true);
        }
        notifyDataSetChanged();
    }




    public interface OnContactClick{
        void onContactClick(Contact message, int position);
    }



}
