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

public class CallMoveMessages extends CallRequest<BaseResponse> implements Callback<BaseResponse> {
    MoveMessagesParameters moveMessagesParameters = new MoveMessagesParameters();

    public CallMoveMessages(CallRequestResult<BaseResponse> callback) {
        super(callback);
    }

    public void setUids(Integer ... uids) {
        String uidsString = "";
        for (Integer uid: uids ) {
            if (!uidsString.isEmpty())
                uidsString = uidsString + ", ";
            uidsString = uidsString +  uid;
        }
        moveMessagesParameters.Uids = uidsString;
    }

    public void setFolder(String folder) {
        moveMessagesParameters.Folder = folder;
    }

    public void setToFolder(String folder) {
        moveMessagesParameters.ToFolder = folder;
    }

    @Override
    public void start() {
        moveMessagesParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();

        Gson gson = new Gson();
        String json = gson.toJson(moveMessagesParameters);

        Call<BaseResponse> call= ApiFactory.getService().moveMessages(ApiModules.MAIL, ApiMethods.MOVE_MESSAGES, json );
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

        if (response.isSuccessful() ) {
            BaseResponse baseResponse = response.body();
            if (baseResponse !=null && baseResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess( baseResponse   );
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
            callback.onFail(ErrorType.FAIL_CONNECT, "", 0);
    }


    class MoveMessagesParameters {
        int AccountID;
        String Folder;
        String ToFolder;
        String Uids;
    }
}
