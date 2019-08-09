package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Group;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetGroupsResponse extends BaseResponse {
    @SerializedName("Result")
    private ArrayList<Group> groups;

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
}