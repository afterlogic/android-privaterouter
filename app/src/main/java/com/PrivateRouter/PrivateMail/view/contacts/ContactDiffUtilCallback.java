package com.PrivateRouter.PrivateMail.view.contacts;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.Message;

public class ContactDiffUtilCallback extends DiffUtil.ItemCallback<Contact> {

    @Override
    public boolean areItemsTheSame(@NonNull Contact contact1, @NonNull Contact contact2) {
        return contact1.getUUID().equals( contact2.getUUID() );
    }

    @Override
    public boolean areContentsTheSame(@NonNull Contact contact1, @NonNull Contact contact2) {
        return contact1.getUUID().equals( contact2.getUUID() ) && contact1.getFullName().equals(contact2.getFullName()) &&
                contact1.getViewEmail().equals(contact2.getViewEmail());
    }
}