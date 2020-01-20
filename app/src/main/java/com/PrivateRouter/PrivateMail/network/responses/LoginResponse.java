package com.PrivateRouter.PrivateMail.network.responses;

import com.google.gson.JsonObject;
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
        TwoFactorAuth twoFactorAuth;

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        public TwoFactorAuth getTwoFactorAuth() {
            return twoFactorAuth;
        }

        public class TwoFactorAuth {
            @SerializedName("UserId")
            int userId;

            public int getUserId() {
                return userId;
            }
        }
    }
}
