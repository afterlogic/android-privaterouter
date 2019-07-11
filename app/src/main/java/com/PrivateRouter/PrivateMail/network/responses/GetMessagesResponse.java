package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.MessageCollection;
import com.google.gson.annotations.SerializedName;

public class GetMessagesResponse extends BaseResponse{
    @SerializedName("Result")
    private
    MessageCollection result;

    public MessageCollection getResult() {
        return result;
    }

    public void setResult(MessageCollection result) {
        this.result = result;
    }
}