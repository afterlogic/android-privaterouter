package com.PrivateRouter.PrivateMail.repository;

import android.support.annotation.NonNull;

import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallGetGroups;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.view.contacts.OnGroupsLoadCallback;

import java.util.ArrayList;


public class GroupsRepository {
    public static volatile GroupsRepository instance = null;
    private ArrayList<Group> groups = null;



    @NonNull
    public static GroupsRepository getInstance() {
        GroupsRepository repository = instance;
        if (repository == null) {
            synchronized (SettingsRepository.class) {
                repository = instance;
                if (repository == null) {
                    repository = instance = new GroupsRepository();
                }
            }
        }
        return repository;
    }


    public void load(OnGroupsLoadCallback onLoadGroups, boolean force) {
        if (!force && groups!=null) {
            onLoadGroups.onGroupsLoad(groups);
        }
        else {
            requestGroup(onLoadGroups);
        }

    }

    private void requestGroup(OnGroupsLoadCallback onLoadGroups) {
        CallGetGroups callGetGroups = new CallGetGroups(new CallRequestResult() {
            @Override
            public void onSuccess(Object result) {
                groups = (ArrayList<Group>) result;
                if (onLoadGroups!=null)
                    onLoadGroups.onGroupsLoad(groups);
            }

            @Override
            public void onFail(ErrorType errorType, String errorString, int serverCode) {
                if (onLoadGroups!=null)
                    onLoadGroups.onGroupsLoadFail(errorType, errorString, serverCode);
            }
        });
        callGetGroups.start();
    }


}
