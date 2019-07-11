package com.PrivateRouter.PrivateMail.network.requests;

abstract public class CallRequest<T> {

    CallRequestResult<T> callback;
    CallRequest(CallRequestResult<T> callback) {
        this.callback = callback;
    }

    abstract public void start();

}
