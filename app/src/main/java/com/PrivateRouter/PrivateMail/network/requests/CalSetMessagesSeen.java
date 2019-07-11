package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalSetMessagesSeen extends CallRequest<BaseResponse>  implements Callback<BaseResponse>
{
    private Message message;

    public CalSetMessagesSeen(CallRequestResult callback){
        super(callback);
    }

    @Override
    public void start() {
        SetMessagesSeenParameters parameters = new SetMessagesSeenParameters();
        parameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();
        parameters.Folder = message.getFolder();
        parameters.SetAction = true;
        parameters.Uids = String.valueOf(message.getUid());


        Gson gson = new Gson();
        String json = gson.toJson(parameters);

        Call<BaseResponse> call= ApiFactory.getService().setEmailSafety(ApiModules.MAIL, ApiMethods.SET_MESSAGES_SEEN, json);
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

    public void setMessage(Message message) {
        this.message = message;
    }

    class SetMessagesSeenParameters {
        int AccountID;
        String Folder;
        String Uids;
        boolean SetAction;
    }

}
