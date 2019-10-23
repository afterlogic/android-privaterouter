package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetMessageResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetMessage extends CallRequest<Message>  implements Callback<GetMessageResponse>
{
    GetMessageParameters getMessageParameters = new GetMessageParameters();

    public CallGetMessage(CallRequestResult callback){
        super(callback);
    }

    public void setFolder(String folder) {
        getMessageParameters.Folder = folder;
    }
    public void setUid(String uid) {
        getMessageParameters.Uid = uid;
    }

    @Override
    public void start(){
        getMessageParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();

        Gson gson = new Gson();
        String json = gson.toJson(getMessageParameters);

        Call<GetMessageResponse> call=ApiFactory.getService().getMessage(ApiModules.MAIL, ApiMethods.GET_MESSAGE, json );
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<GetMessageResponse> call, Response<GetMessageResponse> response) {

        if (response.isSuccessful() ) {
            GetMessageResponse getMessageResponse = response.body();
            if (getMessageResponse !=null && getMessageResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess( getMessageResponse.getResult()  );
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, getMessageResponse.getErrorMessage(), getMessageResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, "", response.code() );
        }
    }

    @Override
    public void onFailure(Call<GetMessageResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT,  "", 0);
    }

    class GetMessageParameters {
        int AccountID;
        String Folder;
        String Uid;
    }

}