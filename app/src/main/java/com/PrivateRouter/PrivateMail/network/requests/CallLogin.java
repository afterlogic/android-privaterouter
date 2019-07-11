package com.PrivateRouter.PrivateMail.network.requests;


import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.LoginResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallLogin extends CallRequest<LoginResponse>  implements Callback<LoginResponse> {
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

    @Override
    public void start() {
        Gson gson = new Gson();
        String json = gson.toJson(loginParameter);

        Call<LoginResponse> call = ApiFactory.getService().login(ApiModules.CORE, ApiMethods.LOGIN, json);
        call.enqueue( this );
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

       if (response.isSuccessful() ) {
           loginResponse = response.body();
           if (loginResponse !=null && loginResponse.isSuccess() ) {

               if (callback!=null)
                   callback.onSuccess(loginResponse);

            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, loginResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code() );
        }
    }


    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        if (callback!=null) {
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
        }
    }


    class LoginParameter {
        String Login;
        String Password;
    }
}
