package com.PrivateRouter.PrivateMail.network.responses;

import com.google.gson.annotations.SerializedName;

public class CreateGroupResponse extends BaseResponse {
    @SerializedName("Result")
    private
    String result;

    public String getResult(){
        return result;
    }

    public void setResult(String result){
        this.result = result;
    }
}
