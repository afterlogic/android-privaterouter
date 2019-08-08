package com.PrivateRouter.PrivateMail.view.contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallGetGroups;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.view.utils.CustomLinearLayoutManager;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupsListActivity extends AppCompatActivity implements OnGroupsLoadCallback, GroupsAdapter.OnGroupClick {
    @BindView(R.id.rv_groups_list)
    RecyclerView rvGroupsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        ButterKnife.bind(this);

        getGroups(this);
        RequestViewUtils.showRequest(this);
    }

    @Override
    public void onGroupsLoad(ArrayList<Group> groups) {
        displayGroups(groups);
        RequestViewUtils.hideRequest();
    }


    @Override
    public void onGroupsLoadFail(ErrorType errorType, int serverCode) {
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this.getApplicationContext(), errorType, serverCode);
    }


    private void getGroups(OnGroupsLoadCallback callback) {
        CallGetGroups callGetGroups = new CallGetGroups(new CallRequestResult() {
            @Override
            public void onSuccess(Object result) {
                callback.onGroupsLoad((ArrayList<Group>) result);
            }

            @Override
            public void onFail(ErrorType errorType, int serverCode) {
                callback.onGroupsLoadFail(errorType, serverCode);
            }
        });
        callGetGroups.start();
    }

    private void displayGroups(ArrayList<Group> groups) {
        GroupsAdapter groupsAdapter = new GroupsAdapter(GroupWorkMode.SINGLE_MODE, groups);
        groupsAdapter.setOnGroupClick(this);
        CustomLinearLayoutManager customLayoutManager = new CustomLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvGroupsList.setLayoutManager(customLayoutManager);

        rvGroupsList.setAdapter(groupsAdapter);
    }

    @Override
    public void onGroupClick(Group group, int position) {
        if (group != null) {

        }
    }

    public enum GroupWorkMode {
        SINGLE_MODE,
        MULTI_MODE
    }

}