package com.PrivateRouter.PrivateMail.network.requests;


import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallPinVerify extends CallRequest<LoginResponse> implements Callback<LoginResponse> {
    LoginParameter loginParameter = new LoginParameter();

    public CallPinVerify(CallRequestResult callback) {
        super(callback);
    }

    public void setLogin(String login) {
        loginParameter.Login = login;
    }

    public void setPassword(String password) {
        loginParameter.Password = password;
    }

    public void setPin(String pin) {
        loginParameter.Pin = pin;
    }

    @Override
    public void start() {
        Gson gson = new Gson();
        String json = gson.toJson(loginParameter);

        Call<LoginResponse> call = ApiFactory.getService().verifyPin(ApiModules.TWO_FACTOR_AUTH, ApiMethods.VERIFY_PIN, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

        if (response.isSuccessful()) {
            LoginResponse loginResponse = response.body();
            if (loginResponse != null && loginResponse.isSuccess()) {

                if (callback != null)
                    callback.onSuccess(loginResponse);

            } else {
                if (callback != null)
                    callback.onFail(ErrorType.ERROR_REQUEST, loginResponse.getErrorMessage(), loginResponse.getErrorCode());
            }

        } else {
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, "", response.code());
        }
    }


    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        if (t instanceof JsonSyntaxException) {
            callback.onFail(ErrorType.ERROR_REQUEST, "", 0);
        } else if (callback != null) {
            callback.onFail(ErrorType.FAIL_CONNECT, "", 0);
        }
    }


    class LoginParameter {
        String Login;
        String Password;
        String Pin;
    }
}
