package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.PrivateRouter.PrivateMail.model.MessageCollection;
import com.PrivateRouter.PrivateMail.model.MessageInfo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetMessageBaseResponse extends BaseResponse{
    @SerializedName("Result")
    private
    ArrayList<MessageBase> result;

    public ArrayList<MessageBase>  getResult() {
        return result;
    }

    public void setResult(ArrayList<MessageBase>  result) {
        this.result = result;
    }
}