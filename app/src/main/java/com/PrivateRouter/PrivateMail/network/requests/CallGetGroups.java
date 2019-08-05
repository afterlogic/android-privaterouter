package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetGroupsResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetGroups extends CallRequest<ArrayList<Group>> implements Callback<GetGroupsResponse> {

    public CallGetGroups(CallRequestResult callback) {
        super(callback);
    }

    @Override
    public void start() {
        Call<GetGroupsResponse> call = ApiFactory.getService().getGroups(ApiModules.CONTACTS, ApiMethods.GET_GROUPS);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<GetGroupsResponse> call, Response<GetGroupsResponse> response) {

        if (response.isSuccessful()) {
            GetGroupsResponse getGroupsResponse = response.body();
            if (getGroupsResponse != null && getGroupsResponse.isSuccess()) {
                if (callback != null) {
                    callback.onSuccess(getGroupsResponse.getGroups());
                }
            } else {
                if (callback != null) {
                    callback.onFail(ErrorType.ERROR_REQUEST, getGroupsResponse.getErrorCode());
                }
            }
        } else {
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code());
        }
    }

    @Override
    public void onFailure(Call<GetGroupsResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }
}