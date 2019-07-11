package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallSetEmailSafety extends CallRequest<BaseResponse>  implements Callback<BaseResponse>
{
    public CallSetEmailSafety(CallRequestResult callback){
        super(callback);
    }

    String email;
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void start() {
        SetEmailSafetyParameters getFolderParameters = new SetEmailSafetyParameters();
        getFolderParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();
        getFolderParameters.Email = email;
        Gson gson = new Gson();
        String json = gson.toJson(getFolderParameters);

        Call<BaseResponse> call= ApiFactory.getService().setEmailSafety(ApiModules.MAIL, ApiMethods.SET_EMAIL_SAFETY, json);
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
                    callback.onFail(ErrorType.ERROR_REQUEST, baseResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code() );
        }
    }

    @Override
    public void onFailure(Call<BaseResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    class SetEmailSafetyParameters {
        int AccountID;
        String Email;
    }
}