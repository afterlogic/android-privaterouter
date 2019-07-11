package com.PrivateRouter.PrivateMail.view.contacts;


import android.provider.ContactsContract;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ContactsListModeMediator {
    private boolean selectionMode = false;

    WeakReference<ContactsActivity> contactsActivity;
    ContactsAdapter contactsAdapter;
    private List<Contact> selected = new LinkedList<>();
    private HashMap<String, Boolean> selectedUUIDs= new HashMap<>();

    public ContactsListModeMediator(ContactsActivity contactsActivity) {
        this.contactsActivity = new WeakReference<>(contactsActivity);
    }



    public boolean isContactSelected(Contact contact) {
        if (contact==null)
            return false;
        else if (selectedUUIDs.get(contact.getUUID())==null)
            return false;
        else
            return selectedUUIDs.get(contact.getUUID());
    }



    public void onSelectChange(boolean value, Contact contact  ) {
        if (value) {
            selected.add(contact);
        }
        else {
            selected.remove(contact);
        }


        if (contactsActivity.get()==null)
            return;


        if (selectedUUIDs.get(contact.getUUID())!=null)
            selectedUUIDs.remove(contact.getUUID());

        selectedUUIDs.put(contact.getUUID(), value);

        contactsActivity.get().onSelectChange(selected);

    }



    public void setAdapter(ContactsAdapter contactsAdapter) {
        this.contactsAdapter = contactsAdapter;
    }

    public void clearSelection() {
        selectedUUIDs.clear();
        selected.clear();
        if (contactsAdapter!=null)
            contactsAdapter.notifyDataSetChanged();
    }

    public List<Contact> getSelectionList() {

        return  selected;
    }

    public List<String> getSelectionUUIDList() {
        List<String> list = new ArrayList<String>();
        list.addAll(selectedUUIDs.keySet());

        return list;
    }

    public void onSelectModeChange(){
        if(contactsActivity.get() == null)
            return;

        if(selectionMode)
            contactsActivity.get().openSelectMode();
        else
            contactsActivity.get().openNormalMode();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean chooseMode) {
        this.selectionMode = chooseMode;
        onSelectModeChange();
    }

    public void closeSelectionMode() {
        if (contactsAdapter!=null) {
            setSelectionMode(false);
            clearSelection();
            contactsAdapter.notifyDataSetChanged();
        }
    }
}

