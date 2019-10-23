package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiError;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetMessagesBodiesResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetMessagesBodies extends CallRequest<ArrayList<Message>>  implements Callback<GetMessagesBodiesResponse>  {
    GetMessagesBodiesParameters parameters = new GetMessagesBodiesParameters();

    public CallGetMessagesBodies(CallRequestResult callback){
        super(callback);
    }
    public CallGetMessagesBodies( ){
        super(null);
    }


    public void setFolder(String folder) {
        parameters.Folder = folder;
    }
    public void setUids(ArrayList<MessageBase> uids) {
        parameters.setUidsFrom(uids);
    }

    Call<GetMessagesBodiesResponse> call;


    @Override
    public void start() {
        if (LoggedUserRepository.getInstance().getActiveAccount()!=null) {
            parameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();

            Gson gson = new Gson();
            String json = gson.toJson(parameters);

            call = ApiFactory.getService().getMessagesBodies(ApiModules.MAIL, ApiMethods.GET_MESSAGES_BODIES, json);
            call.enqueue(this);

        }
    }

    public ArrayList<Message> syncStart()  {
        try {
            if (LoggedUserRepository.getInstance().getActiveAccount() != null) {
                parameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();

                Gson gson = new Gson();
                String json = gson.toJson(parameters);

                call = ApiFactory.getService().getMessagesBodies(ApiModules.MAIL, ApiMethods.GET_MESSAGES_BODIES, json);
                Response<GetMessagesBodiesResponse> response =  call.execute();
                return  response.body().getResult();

            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResponse(Call<GetMessagesBodiesResponse> call, Response<GetMessagesBodiesResponse> response) {

        if (response.isSuccessful() ) {
            GetMessagesBodiesResponse getMessageResponse = response.body();
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
    public void onFailure(Call<GetMessagesBodiesResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ApiError.fromThrowable(t), "",0);
    }

    public void cancel() {
        if (call != null)
            call.cancel();
    }

    class GetMessagesBodiesParameters {
        int AccountID;
        String Folder;
        ArrayList<Integer> Uids;

        public void setUidsFrom(ArrayList<MessageBase> uids) {
            Uids = new ArrayList<>();
            for (MessageBase messageBase: uids) {
                Uids.add(messageBase.getUid());
            }
        }
    }

}