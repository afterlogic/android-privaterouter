package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetGroupResponse;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetGroup extends CallRequest<Group> implements Callback<GetGroupResponse> {
    private String groupUuid;

    CallGetGroup(String groupUuid, CallRequestResult<Group> callback) {
        super(callback);
        this.groupUuid = groupUuid;
    }

    @Override
    public void start() {
        Gson gson = new Gson();
        Parameter parameter = new Parameter();
        parameter.uuid = groupUuid;
        String json = gson.toJson(parameter);
        Call<GetGroupResponse> call = ApiFactory.getService().getGroup(ApiModules.CONTACTS, ApiMethods.GET_GROUP, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<GetGroupResponse> call, Response<GetGroupResponse> response) {
        if (response.isSuccessful()) {
            GetGroupResponse getGroupResponse = response.body();
            if (getGroupResponse != null && getGroupResponse.isSuccess()) {
                if (callback != null) {
                    callback.onSuccess(getGroupResponse.getResult());
                }
            } else if (callback != null) {
                callback.onFail(ErrorType.ERROR_REQUEST, getGroupResponse.getErrorCode());
            }
        } else {
            if (callback != null) {
                callback.onFail(ErrorType.SERVER_ERROR, response.code());
            }
        }
    }

    @Override
    public void onFailure(Call<GetGroupResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    class Parameter {
        @SerializedName("UUID")
        String uuid;
    }
}
