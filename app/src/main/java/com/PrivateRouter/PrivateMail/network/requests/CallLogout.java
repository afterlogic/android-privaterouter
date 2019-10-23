package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallLogout extends CallRequest<BaseResponse>  implements Callback<BaseResponse>
{
    public CallLogout(CallRequestResult callback){
        super(callback);
    }

    @Override
    public void start() {
        Call<BaseResponse> call= ApiFactory.getService().logout(ApiModules.CORE, ApiMethods.LOGOUT);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

        if (response.isSuccessful() ) {
            BaseResponse baseResponse = response.body();
            if (baseResponse !=null && baseResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess(baseResponse  );
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, baseResponse.getErrorMessage(), baseResponse.getErrorCode() );
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