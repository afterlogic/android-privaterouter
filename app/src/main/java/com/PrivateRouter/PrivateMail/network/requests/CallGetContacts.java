package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.UUIDWithETag;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetContactInfoResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetContactsResponse;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;

public class CallGetContacts  {
    GetContactsParameters getContactsParameters = new GetContactsParameters();

    public CallGetContacts(){

    }

    public void setUids(ArrayList<String> uUids) {
        getContactsParameters.Uids = uUids;
    }



    public ArrayList<Contact> syncStart(OnErrorInterface onErrorInterface)  {
        try {

            Gson gson = new Gson();
            String json = gson.toJson(getContactsParameters);

            Call<GetContactsResponse> call = ApiFactory.getService().getContactsByUids(ApiModules.CONTACTS, ApiMethods.GET_CONTACTS_BY_UIDS, json);

            CallExecutor<GetContactsResponse> callExecutor = new CallExecutor<>();
            GetContactsResponse response = callExecutor.execute(call, onErrorInterface);

            if (response!=null && response.getResult()==null)
                response.setResult(new ArrayList<>());

            if (response!=null)
                return  response.getResult();
            else
                return null;


        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    class GetContactsParameters {
        ArrayList<String> Uids;
    }

}