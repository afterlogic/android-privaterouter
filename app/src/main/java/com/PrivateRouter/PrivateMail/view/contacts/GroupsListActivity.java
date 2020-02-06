package com.PrivateRouter.PrivateMail.view.contacts;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Window;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.Storages;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallGetStorage;
import com.PrivateRouter.PrivateMail.repository.GroupsRepository;
import com.PrivateRouter.PrivateMail.view.utils.CustomLinearLayoutManager;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupsListActivity extends AppCompatActivity implements OnGroupsLoadCallback, GroupsAdapter.OnGroupClick, StorageAdapter.OnStorageClick, StoragesCallback {


    public static final int GROUP = 11;

    @BindView(R.id.rv_groups_list)
    RecyclerView rvGroupsList;

    @BindView(R.id.rv_storage_list)
    RecyclerView rvStorageList;
    private Group currentGroup;
    private String currentStorage;
    private ContactsActivity.VIEW_MODE viewMode;

    @NonNull
    public static Intent makeIntent(ContactsActivity activity, Group currentGroup, String currentStorage, ContactsActivity.VIEW_MODE viewMode) {
        Intent intent = new Intent(activity, GroupsListActivity.class);
        intent.putExtra("mode", viewMode);
        intent.putExtra("group", currentGroup);
        intent.putExtra("storage", currentStorage);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        initUI();
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.left_to_right, R.anim.hold);

        parseIntent();

        initStorageList(new ArrayList<Storages>());
        GroupsRepository.getInstance().load(this, true);
        new CallGetStorage(this).execute();

    }

    private void parseIntent() {
        if (getIntent() != null) {
            viewMode = (ContactsActivity.VIEW_MODE) getIntent().getSerializableExtra("mode");
            if (viewMode == ContactsActivity.VIEW_MODE.GROUP)
                currentGroup = (Group) getIntent().getSerializableExtra("group");
            else
                currentStorage = getIntent().getStringExtra("storage");

        }
    }

    private void initStorageList(List<Storages> storages) {

        StorageAdapter storageAdapter = new StorageAdapter(storages, currentStorage);
        storageAdapter.setOnStorageClick(this);

        rvStorageList.setLayoutManager(new LinearLayoutManager(this));
        rvStorageList.setAdapter(storageAdapter);
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    @Override
    public void onGroupsLoad(ArrayList<Group> groups) {
        displayGroups(groups);
        RequestViewUtils.hideRequest();
    }


    @Override
    public void onGroupsLoadFail(ErrorType errorType, String errorString, int serverCode) {
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this.getApplicationContext(), errorType, errorString, serverCode);
    }


    private void displayGroups(ArrayList<Group> groups) {

        GroupsAdapter groupsAdapter = new GroupsAdapter(GroupsAdapter.GroupWorkMode.SINGLE_MODE, groups, currentGroup);
        groupsAdapter.setOnGroupClick(this);
        CustomLinearLayoutManager customLayoutManager = new CustomLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvGroupsList.setLayoutManager(customLayoutManager);

        rvGroupsList.setAdapter(groupsAdapter);
    }


    @Override
    public void onGroupClick(Group group, int position) {
        selectGroup(group);
    }

    private void selectStorage(@NonNull String storageId) {
        Intent intent = new Intent();
        intent.putExtra("SelectedStorage", storageId);
        intent.putExtra("viewGroupMode", false);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void selectGroup(@NonNull Group group) {
        Intent intent = new Intent();
        intent.putExtra("SelectedGroup", group);
        intent.putExtra("viewGroupMode", true);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.right_to_left);
    }

    @Override
    public void onStorageClick(String storage) {
        selectStorage(storage);
    }

    @Override
    public void onStorages(List<Storages> storages) {
        initStorageList(storages);
    }
}