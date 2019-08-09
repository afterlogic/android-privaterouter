package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.CreateGroupResponse;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallCreateGroup extends CallRequest<String> implements Callback<CreateGroupResponse> {
    private Group group;

    public CallCreateGroup(Group group, CallRequestResult<String> callback) {
        super(callback);
        this.group = group;
    }

    @Override
    public void start() {
        Gson gson = new Gson();
        Parameter parameter = new Parameter();
        parameter.group = group;
        String json = gson.toJson(parameter);
        Call<CreateGroupResponse> call = ApiFactory.getService().createGroup(ApiModules.CONTACTS, ApiMethods.CREATE_GROUP, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<CreateGroupResponse> call, Response<CreateGroupResponse> response) {
        if (response.isSuccessful()) {
            CreateGroupResponse createGroupResponse = response.body();
            if (createGroupResponse != null && createGroupResponse.isSuccess()) {
                if (callback != null) {
                    callback.onSuccess(createGroupResponse.getResult());
                }
            } else {
                if (callback != null) {
                    callback.onFail(ErrorType.ERROR_REQUEST, createGroupResponse.getErrorCode());
                }
            }
        } else {
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code());
        }
    }

    @Override
    public void onFailure(Call<CreateGroupResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    class Parameter {
        @SerializedName("Group")
        Group group;

    }
}
