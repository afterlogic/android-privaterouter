package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.UUIDWithETag;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetContactInfoResponse extends BaseResponse {
    @SerializedName("Result")
    private
    Result result;

    public class Result {
        @SerializedName("Info")
        private
        ArrayList<UUIDWithETag> info;

        public ArrayList<UUIDWithETag> getInfo() {
            return info;
        }

        public void setInfo(ArrayList<UUIDWithETag> info) {
            this.info = info;
        }
    }
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
