package com.PrivateRouter.PrivateMail.network.requests;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.CreateContactResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallCreateContact extends CallRequest<String> implements Callback<CreateContactResponse> {
    private Contact contact;

    public CallCreateContact(Contact contact, CallRequestResult<String> callback) {
        super(callback);
        this.contact = contact;
    }

    @Override
    public void start() {
        Gson gson = new Gson();
        String json = gson.toJson(contact);
        Call<CreateContactResponse> call = ApiFactory.getService().getUuid(ApiModules.CONTACTS, ApiMethods.CREATE_CONTACT, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<CreateContactResponse> call, Response<CreateContactResponse> response) {
        if (response.isSuccessful()) {
            CreateContactResponse createContactResponse = response.body();
            if (createContactResponse != null && createContactResponse.isSuccess()) {
                if (callback != null) {
                    callback.onSuccess(createContactResponse.getResult().getUuid());
                }
            } else {
                if (callback != null) {
                    callback.onFail(ErrorType.ERROR_REQUEST, createContactResponse.getErrorCode());
                }
            }
        } else {
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code());
        }
    }

    @Override
    public void onFailure(Call<CreateContactResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }
}
