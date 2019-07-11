package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.PrivateRouter.PrivateMail.model.SyncPeriod;
import com.PrivateRouter.PrivateMail.model.UUIDWithETag;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetContactInfoResponse;
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

public class CallGetContactsInfo  {
    GetContactInfo getContactInfo = new GetContactInfo();

    public CallGetContactsInfo(){

        getContactInfo.Storage = "";
    }

    public void setStorage(String storage) {
        getContactInfo.Storage = storage;
    }



    public ArrayList<UUIDWithETag> syncStart(OnErrorInterface onErrorInterface)  {
        try {

            Gson gson = new Gson();
            String json = gson.toJson(getContactInfo);

            Call<GetContactInfoResponse> call = ApiFactory.getService().getContactInfo(ApiModules.CONTACTS, ApiMethods.GET_CONTACTS_INFO, json);

            CallExecutor<GetContactInfoResponse> callExecutor = new CallExecutor<>();
            GetContactInfoResponse response = callExecutor.execute(call, onErrorInterface);

            if (response!=null && response.getResult()!=null && response.getResult().getInfo()==null)
                response.getResult().setInfo(new ArrayList<>());

            if (response!=null)
                return  response.getResult().getInfo();
            else
                return null;


        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    class GetContactInfo{
        String Storage;
    }

}