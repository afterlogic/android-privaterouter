package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetCTagResponse extends BaseResponse {
    @SerializedName("Result")
    private
    int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

}
