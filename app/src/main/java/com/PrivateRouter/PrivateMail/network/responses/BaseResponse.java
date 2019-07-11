package com.PrivateRouter.PrivateMail.network.responses;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("AuthenticatedUserId")
    private
    int authenticatedUserId;

    @SerializedName("Module")
    private
    String module;

    @SerializedName("ErrorType")
    private
    int errorCode;

    @SerializedName("@Time")
    private
    double time;

    @SerializedName("SubscriptionsResult")
    private
    String SubscriptionsResult;


    public boolean isSuccess() {
        return errorCode == 0;
    }

    public int getAuthenticatedUserId() {
        return authenticatedUserId;
    }

    public void setAuthenticatedUserId(int authenticatedUserId) {
        this.authenticatedUserId = authenticatedUserId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getSubscriptionsResult() {
        return SubscriptionsResult;
    }

    public void setSubscriptionsResult(String subscriptionsResult) {
        SubscriptionsResult = subscriptionsResult;
    }
}
