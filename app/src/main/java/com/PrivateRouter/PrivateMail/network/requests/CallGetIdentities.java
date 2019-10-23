package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.Identities;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetGroupsResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetIdentitiesResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetIdentities  extends CallRequest<ArrayList<Identities>> implements Callback<GetIdentitiesResponse> {

    public CallGetIdentities(CallRequestResult callback) {
        super(callback);
    }

    @Override
    public void start() {
        Call<GetIdentitiesResponse> call = ApiFactory.getService().getIdentities(ApiModules.MAIL, ApiMethods.GET_IDENTITIES);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<GetIdentitiesResponse> call, Response<GetIdentitiesResponse> response) {

        if (response.isSuccessful()) {
            GetIdentitiesResponse getIdentitiesResponse = response.body();
            if (getIdentitiesResponse != null && getIdentitiesResponse.isSuccess()) {
                if (callback != null) {
                    callback.onSuccess(getIdentitiesResponse.getIdentities());
                }
            } else {
                if (callback != null) {
                    callback.onFail(ErrorType.ERROR_REQUEST, getIdentitiesResponse.getErrorMessage(), getIdentitiesResponse.getErrorCode());
                }
            }
        } else {
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, "", response.code());
        }
    }

    @Override
    public void onFailure(Call<GetIdentitiesResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, "",0);
    }
}