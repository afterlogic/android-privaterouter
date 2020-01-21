package com.PrivateRouter.PrivateMail.network.responses;

import com.google.gson.annotations.SerializedName;

public class LoginResponse extends BaseResponse {
    @SerializedName("Result")
    private
    Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        @SerializedName("AuthToken")
        private
        String authToken;

        @SerializedName("TwoFactorAuth")
        Object twoFactorAuth;

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        public Object getTwoFactorAuth() {
            return twoFactorAuth;
        }
    }
}
