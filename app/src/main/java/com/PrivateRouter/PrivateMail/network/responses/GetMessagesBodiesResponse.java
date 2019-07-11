package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Message;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetMessagesBodiesResponse extends BaseResponse{
    @SerializedName("Result")
    private
    ArrayList<Message> result;

    public ArrayList<Message> getResult() {
        return result;
    }

    public void setResult(ArrayList<Message> result) {
        this.result = result;
    }
}