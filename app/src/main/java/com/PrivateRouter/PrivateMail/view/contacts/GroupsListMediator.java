package com.PrivateRouter.PrivateMail.view.contacts;

import com.PrivateRouter.PrivateMail.model.Group;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GroupsListMediator {

    WeakReference<ContactActivity> contactActivity;
    GroupsAdapter groupsAdapter;
    private List<Group> selected = new LinkedList<>();
    private HashMap<String, Boolean> selectedUUIDs = new HashMap<>();

    public GroupsListMediator(ContactActivity contactActivity) {
        this.contactActivity = new WeakReference<>(contactActivity);
    }

    public boolean isGroupSelected(Group group) {
        if (group == null)
            return false;
        else if (selectedUUIDs.get(group.getUUID()) == null)
            return false;
        else
            return selectedUUIDs.get(group.getUUID());
    }

    public void onSelectChange(boolean value, Group group) {
        if (value) {
            selected.add(group);
        } else {
            selected.remove(group);
        }

        if (contactActivity.get() == null)
            return;

        if (selectedUUIDs.get(group.getUUID()) != null)
            selectedUUIDs.remove(group.getUUID());

        selectedUUIDs.put(group.getUUID(), value);
        contactActivity.get().onSelectGroupChange(selected);
    }

    public void setAdapter(GroupsAdapter groupsAdapter) {
        this.groupsAdapter = groupsAdapter;
    }

    public List<Group> getSelectionList() {
        return selected;
    }

    public ArrayList<String> getSelectionUUIDList() {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(selectedUUIDs.keySet());
        return list;
    }
}
