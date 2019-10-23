package com.PrivateRouter.PrivateMail.network.responses;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("AuthenticatedUserId")
    private
    int authenticatedUserId;

    @SerializedName("Module")
    private
    String module;

    @SerializedName("ErrorCode")
    private
    int errorCode;

    @SerializedName("ErrorMessage")
    private
    String errorMessage;


    @SerializedName("@Time")
    private
    double time;

    @SerializedName("SubscriptionsResult")
    private
    String SubscriptionsResult;


    public boolean isSuccess() {
        return errorCode == 0 && TextUtils.isEmpty(errorMessage);
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
