package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.ContactSettings;
import com.google.gson.annotations.SerializedName;

public class GetSettingsResponse extends BaseResponse {
    @SerializedName("Result")
    private
    ContactSettings result;

    public ContactSettings getResult() {
        return result;
    }

    public void setResult(ContactSettings result) {
        this.result = result;
    }
}
