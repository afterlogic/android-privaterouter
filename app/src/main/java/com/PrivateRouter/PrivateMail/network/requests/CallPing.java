package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallPing extends CallRequest<BaseResponse>  implements Callback<BaseResponse>
{

    public CallPing(CallRequestResult callback) {
        super(callback);
    }


    @Override
    public void start() {
        String body = "Module=Core&Method=Ping";
        Call<BaseResponse> call = ApiFactory.getService().ping(ApiModules.CORE, ApiMethods.PING);
        call.enqueue( this );
    }

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

        if (response.isSuccessful() ) {
            BaseResponse loginResponse = response.body();
            if (loginResponse !=null && loginResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess( loginResponse );
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, loginResponse.getErrorMessage(), loginResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, "", response.code() );
        }
    }

    @Override
    public void onFailure(Call<BaseResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT, "",0);
    }
}
