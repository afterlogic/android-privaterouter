package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Account;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetAccountResponse extends BaseResponse{
    @SerializedName("Result")
    private
    ArrayList<Account>  result;

    public ArrayList<Account> getResult() {
        return result;
    }

    public void setResult(ArrayList<Account> result) {
        this.result = result;
    }
}