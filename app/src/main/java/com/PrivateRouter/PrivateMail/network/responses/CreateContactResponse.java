package com.PrivateRouter.PrivateMail.network.responses;

import com.google.gson.annotations.SerializedName;

public class CreateContactResponse extends BaseResponse {
    @SerializedName("Result")
    private
    Result result;

    public class Result {
        @SerializedName("UUID")
        private
        String uuid;

        @SerializedName("ETag")
        private
        String eTag;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getETag() {
            return eTag;
        }

        public void setETag(String eTag) {
            this.eTag = eTag;
        }
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
