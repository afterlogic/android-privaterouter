package com.PrivateRouter.PrivateMail.view.contacts;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Group;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder> {
    private OnGroupClick onGroupClick;
    private ArrayList<Group> groups;
    private GroupsListMediator groupsListMediator;

    GroupsAdapter(ArrayList<Group> groups, GroupsListMediator groupsListMediator){
        this.groups = groups;
        this.groupsListMediator = groupsListMediator;
        groupsListMediator.setAdapter(this);
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        GroupsViewHolder groupsViewHolder = new GroupsViewHolder(view);
        groupsViewHolder.setOnGroupClick(onGroupClick);
        return groupsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder groupsViewHolder, int i) {
        Group group = groups.get(i);
        groupsViewHolder.bind(group, i, this, groupsListMediator.isGroupSelected(group));
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
