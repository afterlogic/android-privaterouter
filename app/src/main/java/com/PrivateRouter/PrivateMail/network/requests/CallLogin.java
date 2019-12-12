package com.PrivateRouter.PrivateMail.network.requests;


import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.DiscoverUrlResponse;
import com.PrivateRouter.PrivateMail.network.responses.LoginResponse;
import com.PrivateRouter.PrivateMail.repository.HostManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.PrivateRouter.PrivateMail.model.errors.ErrorType.REQUEST_HOST;

public class CallLogin extends CallRequest<LoginResponse> implements Callback<LoginResponse> {
    LoginResponse loginResponse;
    LoginParameter loginParameter = new LoginParameter();

    public CallLogin(CallRequestResult callback) {
        super(callback);
    }

    public void setLogin(String login) {
        loginParameter.Login = login;
    }

    public void setPassword(String password) {
        loginParameter.Password = password;
    }

    public void setHost(String host) {
        loginParameter.Host = host;
    }

    @Override
    public void start() {
        if (loginParameter.Host.isEmpty()) {

            String email = loginParameter.Login;
            //todo поменять на https://torguard.tv/pm/
            HostManager.setHost("https://test.afterlogic.com/");
            ApiFactory.getService().discoverHostname(email).enqueue(new Callback<DiscoverUrlResponse>() {
                @Override
                public void onResponse(Call<DiscoverUrlResponse> _call, Response<DiscoverUrlResponse> response) {
                    if (response.body() != null) {
                        String url = response.body().getUrl();
                        if (!url.isEmpty()) {
                            login(url);
                            return;
                        }
                    }
                    if (callback != null) {
                        callback.onFail(REQUEST_HOST, null, 0);
                    }
                }

                @Override
                public void onFailure(Call<DiscoverUrlResponse> call, Throwable t) {
                    if (callback != null) {
                        callback.onFail(REQUEST_HOST, null, 0);
                    }
                }
            });
        } else {
            login(loginParameter.Host);
        }
    }

    private void login(String host) {
        HostManager.setHost(host);
        Gson gson = new Gson();
        String json = gson.toJson(loginParameter);

        Call<LoginResponse> call = ApiFactory.getService().login(ApiModules.CORE, ApiMethods.LOGIN, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

        if (response.isSuccessful()) {
            loginResponse = response.body();
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
        if (callback != null) {
            callback.onFail(ErrorType.FAIL_CONNECT, "", 0);
        }
    }


    class LoginParameter {
        String Login;
        String Password;
        String Host;
    }
}
