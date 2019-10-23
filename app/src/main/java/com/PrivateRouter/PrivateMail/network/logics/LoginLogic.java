package com.PrivateRouter.PrivateMail.network.logics;

import android.content.Context;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
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
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

public class LoginLogic implements CallRequestResult<LoginResponse> {

    public  interface OnLoginCallback {
        void onLogin();
        void onFail(ErrorType failConnect, String errorString, int errorCode);
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
        if (result==null || result.getResult() == null) {
            Context context = PrivateMailApplication.getContext();
            String errorString = context.getString(R.string.login_error);
            if (result!=null)
                errorString = result.getErrorMessage();
            if (onLogin!=null)
                onLogin.onFail(ErrorType.SERVER_ERROR, errorString, result.getErrorCode());
            return;
        }
        String token = result.getResult().getAuthToken();
        ApiFactory.setToken( token );
        LoggedUserRepository.getInstance().setAuthToken(token);

        loadAccounts();
    }

    @Override
    public void onFail(ErrorType errorType, String errorString,  int serverCode) {
        if (onLogin!=null)
            onLogin.onFail(errorType, errorString, serverCode);
    }

    private void loadAccounts() {
        CallGetAccounts callGetAccounts = new CallGetAccounts(new CallRequestResult<GetAccountResponse>() {
            @Override
            public void onSuccess(GetAccountResponse result) {
                User user = new User();
                user.setAccounts(result.getResult() );
                LoggedUserRepository.getInstance().setUser(user);
                LoggedUserRepository.getInstance().setLogin( login );

                PrivateMailApplication.getInstance().getIdentitiesRepository().init();
                loadFolders();
            }


            @Override
            public void onFail(ErrorType errorCode, String errorString,  int serverCode) {
                if (onLogin!=null)
                    onLogin.onFail(ErrorType.FAIL_CONNECT, errorString, 0);
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
        }, (errorType, errorString, errorCode) -> {
            if (onLogin != null)
                onLogin.onFail(ErrorType.FAIL_CONNECT, errorString,0);
        });
        loadFolderLogic.execute();


    }



}
