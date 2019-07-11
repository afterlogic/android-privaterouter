package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Message;
import com.google.gson.annotations.SerializedName;

public class GetMessageResponse  extends BaseResponse{
    @SerializedName("Result")
    private
    Message result;

    public Message getResult() {
        return result;
    }

    public void setResult(Message result) {
        this.result = result;
    }
}