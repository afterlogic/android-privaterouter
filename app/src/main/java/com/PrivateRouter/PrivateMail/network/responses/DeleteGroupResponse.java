package com.PrivateRouter.PrivateMail.network.responses;

import com.google.gson.annotations.SerializedName;

public class DeleteGroupResponse extends BaseResponse {
    @SerializedName("Result")
    private
    Boolean result;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}