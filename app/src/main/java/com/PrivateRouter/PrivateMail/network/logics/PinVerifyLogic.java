package com.PrivateRouter.PrivateMail.network.logics;

import android.content.Context;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.User;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.requests.CallGetAccounts;
import com.PrivateRouter.PrivateMail.network.requests.CallPinVerify;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.network.responses.GetAccountResponse;
import com.PrivateRouter.PrivateMail.network.responses.LoginResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;

public class PinVerifyLogic implements CallRequestResult<LoginResponse> {

    public interface OnVerifyCallback {
        void onLogin();

        void onFail(ErrorType failConnect, String errorString, int errorCode);
    }

    OnVerifyCallback onVerify;
    String login;
    String pass;
    Context context;

    public PinVerifyLogic(Context context, String login, String pass) {
        this.context = context;
        this.login = login;
        this.pass = pass;
    }

    public void verify(String pin, OnVerifyCallback onVerifyCallback) {
        this.onVerify = onVerifyCallback;
        CallPinVerify callLogin = new CallPinVerify(this);
        callLogin.setLogin(login);
        callLogin.setPassword(pass);
        callLogin.setPin(pin);
        callLogin.start();
    }


    @Override
    public void onSuccess(LoginResponse result) {
        if (result == null || result.getResult() == null) {
            Context context = PrivateMailApplication.getContext();
            String errorString = context.getString(R.string.login_error);
            if (result != null)
                errorString = result.getErrorMessage();
            if (onVerify != null)
                onVerify.onFail(ErrorType.SERVER_ERROR, errorString, result.getErrorCode());
            return;
        }
        String token = result.getResult().getAuthToken();
        ApiFactory.setToken(token);
        LoggedUserRepository.getInstance().setAuthToken(token);

        loadAccounts();
    }

    @Override
    public void onFail(ErrorType errorType, String errorString, int serverCode) {
        if (onVerify != null) {
            onVerify.onFail(errorType, errorString, serverCode);
        }
    }

    private void loadAccounts() {
        CallGetAccounts callGetAccounts = new CallGetAccounts(new CallRequestResult<GetAccountResponse>() {
            @Override
            public void onSuccess(GetAccountResponse result) {
                User user = new User();
                user.setAccounts(result.getResult());
                LoggedUserRepository.getInstance().setUser(user);
                LoggedUserRepository.getInstance().setLogin(login);

                PrivateMailApplication.getInstance().getIdentitiesRepository().init();
                loadFolders();
            }


            @Override
            public void onFail(ErrorType errorCode, String errorString, int serverCode) {
                if (onVerify != null)
                    onVerify.onFail(ErrorType.FAIL_CONNECT, errorString, 0);
            }
        });
        callGetAccounts.start();
    }


    private void loadFolders() {

        LoadFolderLogic loadFolderLogic = new LoadFolderLogic(folderCollection -> {
            Account account = LoggedUserRepository.getInstance().getActiveAccount();
            account.setFolders(folderCollection);

            LoggedUserRepository.getInstance().save(context);

            if (onVerify != null)
                onVerify.onLogin();
        }, (errorType, errorString, errorCode) -> {
            if (onVerify != null)
                onVerify.onFail(ErrorType.FAIL_CONNECT, errorString, 0);
        });
        loadFolderLogic.execute();


    }


}
