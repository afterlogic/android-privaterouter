package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.UUIDWithETag;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetCTagResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetContactInfoResponse;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;


public class CallGetCTag {
    Paramters paramters = new Paramters();
    public  static  final  int FAIL = -100;

    public CallGetCTag(){

        paramters.Storage = "";
    }

    public void setStorage(String storage) {
        paramters.Storage = storage;
    }



    public int syncStart(OnErrorInterface onErrorInterface)  {
        try {

            Gson gson = new Gson();
            String json = gson.toJson(paramters);

            Call<GetCTagResponse> call = ApiFactory.getService().getCTag(ApiModules.CONTACTS, ApiMethods.GET_C_TAG, json);

            CallExecutor<GetCTagResponse> callExecutor = new CallExecutor<>();
            GetCTagResponse response = callExecutor.execute(call, onErrorInterface);


            if (response!=null)
                return  response.getResult();
            else
                return FAIL;


        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return FAIL;
    }

    class Paramters{
        String Storage;
    }

}
