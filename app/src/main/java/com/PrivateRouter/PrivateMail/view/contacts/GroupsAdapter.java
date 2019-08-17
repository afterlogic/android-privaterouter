package com.PrivateRouter.PrivateMail.view.contacts;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Group;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder> {
    private Group selectedElement;

    public enum GroupWorkMode {
        SINGLE_MODE,
        CHECK_MODE
    }

    private OnGroupClick onGroupClick;
    private ArrayList<Group> groups;
    private GroupsListMediator groupsListMediator;
    private GroupWorkMode groupsMode;

    GroupsAdapter(GroupWorkMode groupsMode, ArrayList<Group> groups, GroupsListMediator groupsListMediator){
        this.groupsMode = groupsMode;
        this.groups = groups;
        this.groupsListMediator = groupsListMediator;
        groupsListMediator.setAdapter(this);
    }

    GroupsAdapter(GroupWorkMode groupsMode, ArrayList<Group> groups, Group selectedElement){
        this.groupsMode = groupsMode;
        this.groups = groups;
        this.selectedElement = selectedElement;
    }


    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        GroupsViewHolder groupsViewHolder = new GroupsViewHolder(view, groupsMode);
        groupsViewHolder.setOnGroupClick(onGroupClick);
        return groupsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder groupsViewHolder, int i) {
        Group group = groups.get(i);
        boolean selected = selectedElement !=null  && (group.getUUID().equals(selectedElement.getUUID()));
        groupsViewHolder.bind(group, i, this, groupsListMediator != null && groupsListMediator.isGroupSelected(group), selected );
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void onSelectChange(boolean value, Group group) {

        groupsListMediator.onSelectChange(value, group);
    }

    public void setOnGroupClick(OnGroupClick onGroupClick){
        this.onGroupClick = onGroupClick;
    }

    public OnGroupClick getOnGroupClick(){
        return onGroupClick;
    }

    public interface OnGroupClick{
        void onGroupClick(Group group, int position);
    }


}
