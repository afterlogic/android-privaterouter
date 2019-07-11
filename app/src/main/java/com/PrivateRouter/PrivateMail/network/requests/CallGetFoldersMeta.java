package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.FolderMeta;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetFoldersMetaResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetFoldersMeta extends CallRequest<HashMap<String, FolderMeta> >  implements Callback<GetFoldersMetaResponse>
{
    String json;

    public CallGetFoldersMeta(CallRequestResult callback, ArrayList<String> folders){
        super(callback);
        GetRelevantParameters getRelevantParameters = new GetRelevantParameters();
        getRelevantParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();
        getRelevantParameters.Folders = folders;

        Gson gson = new Gson();
        json = gson.toJson(getRelevantParameters);
    }

    @Override
    public void start(){

        Call<GetFoldersMetaResponse> call= ApiFactory.getService().getRelevantFoldersInformation(ApiModules.MAIL, ApiMethods.GET_RELEVANT_FOLDERS_INFORMATION, json);
        call.enqueue(this);
    }

    public GetFoldersMetaResponse startSync(OnErrorInterface onFailCallback) {
        Call<GetFoldersMetaResponse> call= ApiFactory.getService().getRelevantFoldersInformation(ApiModules.MAIL, ApiMethods.GET_RELEVANT_FOLDERS_INFORMATION, json);
        CallExecutor<GetFoldersMetaResponse> callExecutor = new CallExecutor<GetFoldersMetaResponse>();
        BaseResponse response = callExecutor.execute(call, onFailCallback);

        return (GetFoldersMetaResponse) response;
    }

    @Override
    public void onResponse(Call<GetFoldersMetaResponse>  call, Response<GetFoldersMetaResponse> response) {

        if (response.isSuccessful() ) {
            GetFoldersMetaResponse getAccountResponse = response.body();
            if (getAccountResponse !=null && getAccountResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess( getAccountResponse.getFolderMeta()  );
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, getAccountResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code() );
        }
    }



    @Override
    public void onFailure(Call<GetFoldersMetaResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    class GetRelevantParameters {
        int AccountID;
        ArrayList<String> Folders;
    }
}