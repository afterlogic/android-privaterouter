package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.ContactSettings;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetSettingsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CallGetSettings extends CallRequest<ContactSettings> implements Callback<GetSettingsResponse> {
    public CallGetSettings(CallRequestResult<ContactSettings> callback) {
        super(callback);
    }

    @Override
    public void start() {
        Call<GetSettingsResponse> call = ApiFactory.getService().getSettings(ApiModules.CONTACTS, ApiMethods.GET_SETTINGS);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<GetSettingsResponse> call, Response<GetSettingsResponse> response) {
        if(response.isSuccessful()){
            GetSettingsResponse getSettingsResponse = response.body();
            if(getSettingsResponse !=null && getSettingsResponse.isSuccess()){
                if(callback != null)
                    callback.onSuccess( getSettingsResponse.getResult() );
            }
            else {
                if(callback != null)
                    callback.onFail(ErrorType.ERROR_REQUEST, getSettingsResponse.getErrorMessage(), getSettingsResponse.getErrorCode());
            }
        }
        else{
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, "", response.code());
        }

    }

    @Override
    public void onFailure(Call<GetSettingsResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, "", 0);

    }
}
