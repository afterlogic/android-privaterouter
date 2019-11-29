package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Storages;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.GetStoragesResponse;

import java.util.List;

import retrofit2.Call;


public class CallGetCTag {

    public static final int FAIL = -100;


    public List<Storages> syncStart(OnErrorInterface onErrorInterface) {
        try {


            Call<GetStoragesResponse> call = ApiFactory.getService().getContactStorages(ApiModules.CONTACTS, ApiMethods.GET_CONTACT_STORAGES);

            CallExecutor<GetStoragesResponse> callExecutor = new CallExecutor<>();
            List<Storages> response = callExecutor.execute(call, onErrorInterface).getStorages();


            return response;


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
