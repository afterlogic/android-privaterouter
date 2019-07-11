package com.PrivateRouter.PrivateMail.network.logics;

import android.content.Context;

import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.FolderCollection;
import com.PrivateRouter.PrivateMail.model.User;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.requests.CallGetAccounts;
import com.PrivateRouter.PrivateMail.network.requests.CallGetFolder;
import com.PrivateRouter.PrivateMail.network.requests.CallLogin;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.network.responses.GetAccountResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetFolderResponse;
import com.PrivateRouter.PrivateMail.network.responses.LoginResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;

public class LoginLogic implements CallRequestResult<LoginResponse> {

    public  interface OnLoginCallback {
        void onLogin();
        void onFail(ErrorType failConnect, int errorCode);
    }
    OnLoginCallback onLogin;
    String login;
    Context context;

    public LoginLogic(Context context) {
        this.context = context;
    }

    public void login(String login, String pass, OnLoginCallback onLoginCallback) {
        this.onLogin = onLoginCallback;
        this.login = login;

        CallLogin callLogin = new CallLogin(this);
        callLogin.setLogin(login);
        callLogin.setPassword(pass);
        callLogin.start();
    }

    @Override
    public void onSuccess(LoginResponse result) {

        String token = result.getResult().getAuthToken();
        ApiFactory.setToken( token );
        LoggedUserRepository.getInstance().setAuthToken(token);

        loadAccounts();
    }

    @Override
    public void onFail(ErrorType errorType, int serverCode) {
        if (onLogin!=null)
            onLogin.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    private void loadAccounts() {
        CallGetAccounts callGetAccounts = new CallGetAccounts(new CallRequestResult<GetAccountResponse>() {
            @Override
            public void onSuccess(GetAccountResponse result) {
                User user = new User();
                user.setAccounts(result.getResult() );
                LoggedUserRepository.getInstance().setUser(user);
                LoggedUserRepository.getInstance().setLogin( login );

                loadFolders();
            }


            @Override
            public void onFail(ErrorType errorCode, int serverCode) {
                if (onLogin!=null)
                    onLogin.onFail(ErrorType.FAIL_CONNECT, 0);
            }
        });
        callGetAccounts.start();

    }



    private void loadFolders() {

        LoadFolderLogic loadFolderLogic = new LoadFolderLogic(folderCollection -> {
            Account account = LoggedUserRepository.getInstance().getActiveAccount();
            account.setFolders( folderCollection );

            LoggedUserRepository.getInstance().save( context );

            if (onLogin!=null)
                onLogin.onLogin();
        }, (errorType, errorCode) -> {
            if (onLogin != null)
                onLogin.onFail(ErrorType.FAIL_CONNECT, 0);
        });
        loadFolderLogic.execute();


    }



}
