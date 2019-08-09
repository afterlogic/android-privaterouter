package com.PrivateRouter.PrivateMail.view.contacts;

import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;

import java.util.ArrayList;

public interface OnGroupsLoadCallback {
    void onGroupsLoad(ArrayList<Group> groups);

    void onGroupsLoadFail(ErrorType errorType, int serverCode);
}
