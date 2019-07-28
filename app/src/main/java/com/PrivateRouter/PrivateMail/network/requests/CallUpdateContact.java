package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.UpdateContactResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallUpdateContact extends CallRequest<Boolean> implements Callback<UpdateContactResponse> {
    private Contact contact;

    public CallUpdateContact(Contact contact, CallRequestResult<Boolean> callback) {
        super(callback);
        this.contact = contact;
    }

    @Override
    public void start() {
        Gson gson = new Gson();
        String json = gson.toJson(contact);
        Call<UpdateContactResponse> call = ApiFactory.getService().updateContact(ApiModules.CONTACTS, ApiMethods.UPDATE_CONTACT, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<UpdateContactResponse> call, Response<UpdateContactResponse> response) {
        if (response.isSuccessful()) {
            UpdateContactResponse updateContactResponse = response.body();
            if (updateContactResponse != null && updateContactResponse.isSuccess()) {
                if (callback != null) {
                    callback.onSuccess(updateContactResponse.getResult());
                }
            } else if (callback != null) {
                callback.onFail(ErrorType.ERROR_REQUEST, updateContactResponse.getErrorCode());

            }
        } else {
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code());
        }
    }


    @Override
    public void onFailure(Call<UpdateContactResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }
}
