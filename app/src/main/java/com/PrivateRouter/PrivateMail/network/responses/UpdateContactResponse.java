package com.PrivateRouter.PrivateMail.network.responses;

import com.google.gson.annotations.SerializedName;

public class UpdateContactResponse extends BaseResponse {
    @SerializedName("Result")
    private
    boolean result;

    public boolean getResult(){
        return result;
    }

    public void setResult(boolean result){
        this.result = result;
    }

}
