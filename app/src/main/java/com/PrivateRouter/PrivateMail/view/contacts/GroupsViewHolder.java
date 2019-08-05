package com.PrivateRouter.PrivateMail.view.contacts;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Group;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupsViewHolder extends RecyclerView.ViewHolder {

    boolean checked;
    private int position;
    private Group group;
    GroupsAdapter groupsAdapter;

    @BindView(R.id.item_group)
    CheckedTextView ctvItem;

    private GroupsAdapter.OnGroupClick onGroupClick;

    public GroupsAdapter.OnGroupClick getOnGroupClick() {
        return onGroupClick;
    }

    public void setOnGroupClick(GroupsAdapter.OnGroupClick onGroupClick) {
        this.onGroupClick = onGroupClick;
    }

    public GroupsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctvItem.isChecked()) {
                    ctvItem.setChecked(false);
                } else {
                    ctvItem.setChecked(true);
                }
                groupsAdapter.onSelectChange(ctvItem.isChecked(), group);
            }
        });
    }

    public void bind(Group group, int position, GroupsAdapter groupsAdapter, boolean checked) {
        this.group = group;
        this.position = position;
        this.groupsAdapter = groupsAdapter;
        this.checked = checked;
        if (group != null) {
            ctvItem.setText("# " + group.getName());
            ctvItem.setChecked(checked);
        }
    }
}
