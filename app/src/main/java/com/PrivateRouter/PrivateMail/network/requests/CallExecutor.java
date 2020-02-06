package com.PrivateRouter.PrivateMail.network.requests;

import androidx.annotation.NonNull;

import com.PrivateRouter.PrivateMail.model.errors.ErrorCodes;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class CallExecutor<T> {
    public T execute(@NonNull Call<T> call, OnErrorInterface onFailCallback) {

        try {
            Response<T> response = call.execute();
            if (response.isSuccessful() ) {
                T body = response.body();

                int errorCode = ErrorCodes.UNKNOWN;
                String errorString = "";
                if (body != null && body instanceof BaseResponse) {
                    errorCode = ((BaseResponse)body).getErrorCode();
                    errorString = ((BaseResponse)body).getErrorMessage();
                }

                if (errorCode==0) {
                    return body;
                }
                else if (onFailCallback != null)
                    onFailCallback.onError(ErrorType.ERROR_REQUEST, errorString, errorCode);

            }
            else if (onFailCallback != null)
                onFailCallback.onError(ErrorType.SERVER_ERROR, "", response.code());


        } catch (IOException e) {
            e.printStackTrace();
            if (onFailCallback != null)
                onFailCallback.onError(ErrorType.FAIL_CONNECT, "", 0);
        }
        return null;
    }
}
