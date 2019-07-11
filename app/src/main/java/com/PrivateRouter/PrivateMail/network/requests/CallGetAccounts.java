package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetAccountResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetAccounts extends CallRequest<GetAccountResponse>  implements Callback<GetAccountResponse>
{
    public CallGetAccounts(CallRequestResult callback){
        super(callback);
    }

    @Override
    public void start(){

        Call<GetAccountResponse> call=ApiFactory.getService().getAccounts(ApiModules.MAIL, ApiMethods.GET_ACCOUNTS);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<GetAccountResponse> call, Response<GetAccountResponse> response) {

        if (response.isSuccessful() ) {
            GetAccountResponse getAccountResponse = response.body();
            if (getAccountResponse !=null && getAccountResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess(getAccountResponse  );
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, getAccountResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code() );
        }
    }

    @Override
    public void onFailure(Call<GetAccountResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }
}