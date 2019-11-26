package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;

public interface CallRequestResult<T> {
    void onSuccess(T result);

    void onFail(ErrorType errorType, String serverError, int serverCode);
}
