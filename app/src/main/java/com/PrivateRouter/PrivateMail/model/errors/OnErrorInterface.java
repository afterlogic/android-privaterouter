package com.PrivateRouter.PrivateMail.model.errors;

public interface OnErrorInterface {
    void onError(ErrorType errorType, String errorString, int errorCode);
}
