package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetFolderResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetFolder extends CallRequest<GetFolderResponse>  implements Callback<GetFolderResponse>
{
    public CallGetFolder(CallRequestResult callback){
        super(callback);
    }

    @Override
    public void start(){
        GetFolderParameters getFolderParameters = new GetFolderParameters();
        getFolderParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();
        Gson gson = new Gson();
        String json = gson.toJson(getFolderParameters);


        Call<GetFolderResponse> call= ApiFactory.getService().getFolder(ApiModules.MAIL, ApiMethods.GET_FOLDERS, json);
        call.enqueue(this);
    }

    public GetFolderResponse startSync(OnErrorInterface onFailCallback){
        GetFolderParameters getFolderParameters = new GetFolderParameters();
        getFolderParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();
        Gson gson = new Gson();
        String json = gson.toJson(getFolderParameters);


        Call<GetFolderResponse> call= ApiFactory.getService().getFolder(ApiModules.MAIL, ApiMethods.GET_FOLDERS, json);
        CallExecutor<GetFolderResponse> callExecutor = new CallExecutor<GetFolderResponse>();
        BaseResponse response = callExecutor.execute(call, onFailCallback);

        return (GetFolderResponse) response;
    }

    @Override
    public void onResponse(Call<GetFolderResponse> call, Response<GetFolderResponse> response) {

        if (response.isSuccessful() ) {
            GetFolderResponse getAccountResponse = response.body();
            if (getAccountResponse !=null && getAccountResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess(getAccountResponse  );
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, getAccountResponse.getErrorMessage(), getAccountResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, "", response.code() );
        }
    }

    @Override
    public void onFailure(Call<GetFolderResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT, "", 0);
    }

    class GetFolderParameters {
        int AccountID;
    }
}