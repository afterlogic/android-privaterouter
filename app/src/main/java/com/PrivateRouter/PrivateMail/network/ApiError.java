package com.PrivateRouter.PrivateMail.network;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;

public class ApiError {
    public static ErrorType fromThrowable(Throwable throwable) {
        if (throwable!=null){
            if (throwable instanceof java.net.SocketTimeoutException ) {
                return ErrorType.TIMEOUT;
            }
        }
        return ErrorType.FAIL_CONNECT;
    }
}
