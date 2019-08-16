package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.PrivateRouter.PrivateMail.model.SyncPeriod;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetMessageBaseResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallGetMessagesBase extends CallRequest<ArrayList<MessageBase>>  implements Callback<GetMessageBaseResponse>
{
    GetMessageParametersInfo getMessageParameters = new GetMessageParametersInfo();
    private int iteration;

    public CallGetMessagesBase(){
        super(null);
        getMessageParameters.Search = "";
    }

    public void setFolder(String folder) {
        getMessageParameters.Folder = folder;
    }

    Call<GetMessageBaseResponse> call;

    @Override
    public void start(){
        if (LoggedUserRepository.getInstance().getActiveAccount()!=null) {
            initParameters();

            getMessageParameters.UseThreading = true;
            getMessageParameters.SortBy = "date";
            Gson gson = new Gson();
            String json = gson.toJson(getMessageParameters);

            call = ApiFactory.getService().getMessagesInfo(ApiModules.MAIL, ApiMethods.GET_MESSAGES_INFO, json);
            call.enqueue(this);
        }
    }

    private void initParameters() {
        getMessageParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();

        int period = SettingsRepository.getInstance().getSyncPeriod(PrivateMailApplication.getContext());
        SyncPeriod syncPeriod = SyncPeriod.values()[period];
        int monthAgo = syncPeriod.getMonth();

        String search = "";
        if (monthAgo>0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date() );
            calendar.add(Calendar.MONTH, -monthAgo*(iteration+1));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
            search = "date:"+ simpleDateFormat.format( calendar.getTime() )+"/";
            //"Search":"date:2019.02.05/"
        }
        getMessageParameters.Search = search;
    }


    public ArrayList<MessageBase> syncStart(OnErrorInterface onErrorInterface)  {
        try {
            if (LoggedUserRepository.getInstance().getActiveAccount() != null) {
                initParameters();

                getMessageParameters.UseThreading = true;
                getMessageParameters.SortBy = "date";

                Gson gson = new Gson();
                String json = gson.toJson(getMessageParameters);


                call = ApiFactory.getService().getMessagesInfo (ApiModules.MAIL, ApiMethods.GET_MESSAGES_INFO, json);

                CallExecutor<GetMessageBaseResponse> callExecutor = new CallExecutor<GetMessageBaseResponse>();
                GetMessageBaseResponse response = callExecutor.execute(call, onErrorInterface);

                if (response!=null && response.getResult()==null)
                    response.setResult(new ArrayList<>());

                if (response!=null)
                    return  response.getResult();
                else
                    return null;

            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResponse(Call<GetMessageBaseResponse> call, Response<GetMessageBaseResponse> response) {

        if (response.isSuccessful() ) {
            GetMessageBaseResponse getMessageBaseResponse = response.body();
            if (getMessageBaseResponse !=null && getMessageBaseResponse.isSuccess() ) {

                if (callback!=null)
                    callback.onSuccess(getMessageBaseResponse.getResult());
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, getMessageBaseResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code() );
        }
    }

    @Override
    public void onFailure(Call<GetMessageBaseResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    public void cancel() {
        if (call!=null)
            call.cancel();
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    class GetMessageParametersInfo {
        int AccountID;
        String Folder;
        String Search;
        Boolean UseThreading;
        String SortBy;
    }

}