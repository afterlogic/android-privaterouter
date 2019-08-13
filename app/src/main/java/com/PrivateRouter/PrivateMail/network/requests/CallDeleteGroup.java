package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.DeleteGroupResponse;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallDeleteGroup extends CallRequest<Boolean> implements Callback<DeleteGroupResponse> {
    private String groupUuid;

    public CallDeleteGroup(String groupUuid, CallRequestResult<Boolean> callback) {
        super(callback);
        this.groupUuid = groupUuid;
    }

    @Override
    public void start() {
        Gson gson = new Gson();
        Parameter parameter = new Parameter();
        parameter.uuid = groupUuid;
        String json = gson.toJson(parameter);
        Call<DeleteGroupResponse> call = ApiFactory.getService().deleteGroup(ApiModules.CONTACTS, ApiMethods.DELETE_GROUP, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<DeleteGroupResponse> call, Response<DeleteGroupResponse> response) {
        if (response.isSuccessful()) {
            DeleteGroupResponse deleteGroupResponse = response.body();
            if (deleteGroupResponse != null && deleteGroupResponse.isSuccess()) {
                if (callback != null) {
                    callback.onSuccess(deleteGroupResponse.getResult());
                }
            } else if (callback != null) {
                callback.onFail(ErrorType.ERROR_REQUEST, deleteGroupResponse.getErrorCode());
            }
        } else {
            if (callback != null) {
                callback.onFail(ErrorType.SERVER_ERROR, response.code());
            }
        }
    }

    @Override
    public void onFailure(Call<DeleteGroupResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    class Parameter {
        @SerializedName("UUID")
        String uuid;
    }
}
