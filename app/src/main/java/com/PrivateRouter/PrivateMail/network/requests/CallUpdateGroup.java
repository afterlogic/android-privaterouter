package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.UpdateGroupResponse;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallUpdateGroup extends CallRequest<Boolean> implements Callback<UpdateGroupResponse> {
    private Group group;

    public CallUpdateGroup(Group group, CallRequestResult<Boolean> callback) {
        super(callback);
        this.group = group;
    }

    @Override
    public void start() {
        Gson gson = new Gson();
        Parameter parameter = new Parameter();
        parameter.group = group;
        String json = gson.toJson(parameter);
        Call<UpdateGroupResponse> call = ApiFactory.getService().updateGroup(ApiModules.CONTACTS, ApiMethods.UPDATE_GROUP, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<UpdateGroupResponse> call, Response<UpdateGroupResponse> response) {
        if (response.isSuccessful()) {
            UpdateGroupResponse updateGroupResponse = response.body();
            if (updateGroupResponse != null && updateGroupResponse.isSuccess()) {
                if (callback != null) {
                    callback.onSuccess(updateGroupResponse.getResult());
                }
            } else if (callback != null) {
                callback.onFail(ErrorType.ERROR_REQUEST, updateGroupResponse.getErrorCode());
            }
        } else {
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code());
        }
    }

    @Override
    public void onFailure(Call<UpdateGroupResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    class Parameter {
        @SerializedName("Group")
        Group group;

    }
}
