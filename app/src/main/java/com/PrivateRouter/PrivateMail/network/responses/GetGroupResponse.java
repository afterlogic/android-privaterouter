package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Group;
import com.google.gson.annotations.SerializedName;

public class GetGroupResponse extends BaseResponse {
    @SerializedName("Result")
    private
    Group result;

    public Group getResult() {
        return result;
    }

    public void setResult(Group result) {
        this.result = result;
    }
}