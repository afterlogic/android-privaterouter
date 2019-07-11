package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.UUIDWithETag;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetContactsResponse  extends BaseResponse {
    @SerializedName("Result")
    private
    ArrayList<Contact> result;

    public ArrayList<Contact> getResult() {
        return result;
    }

    public void setResult(ArrayList<Contact> result) {
        this.result = result;
    }
}
