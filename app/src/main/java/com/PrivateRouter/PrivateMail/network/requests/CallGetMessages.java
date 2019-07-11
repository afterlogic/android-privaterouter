package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.MessageCollection;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetMessagesResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetMessages extends CallRequest<MessageCollection>  implements Callback<GetMessagesResponse>
{
    GetMessageParameters getMessageParameters = new GetMessageParameters();

    public CallGetMessages(CallRequestResult callback){
        super(callback);

        getMessageParameters.Offset = 0;
        getMessageParameters.Limit = 200;
        getMessageParameters.Search = "";
        getMessageParameters.Filters = "";
        getMessageParameters.UseThreading = false;
    }

    public void setFolder(String folder) {
        getMessageParameters.Folder = folder;
    }

    public void setOffset(int offset) {
        getMessageParameters.Offset = offset;
    }


    public void setLimit(int limit) {
        getMessageParameters.Limit = limit;
    }

    Call<GetMessagesResponse> call;

    @Override
    public void start(){
        if (LoggedUserRepository.getInstance().getActiveAccount()!=null) {
            getMessageParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();

            Gson gson = new Gson();
            String json = gson.toJson(getMessageParameters);

            call = ApiFactory.getService().getMessages(ApiModules.MAIL, ApiMethods.GET_MESSAGES, json);
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<GetMessagesResponse> call, Response<GetMessagesResponse> response) {

        if (response.isSuccessful() ) {
            GetMessagesResponse getMessagesResponse = response.body();
            if (getMessagesResponse !=null && getMessagesResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess( getMessagesResponse.getResult()  );
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, getMessagesResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code() );
        }
    }

    @Override
    public void onFailure(Call<GetMessagesResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    public void cancel() {
        if (call!=null)
            call.cancel();
    }

    class GetMessageParameters {
        int AccountID;
        String Folder;
        int Offset;
        int Limit;
        String Search;
        String Filters;
        boolean UseThreading;
    }

}