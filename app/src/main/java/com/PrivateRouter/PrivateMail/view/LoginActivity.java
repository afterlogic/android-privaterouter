package com.PrivateRouter.PrivateMail.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.EditText;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.logics.LoginLogic;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginLogic.OnLoginCallback {

    @BindView(R.id.et_email)
    EditText etEmail;

    @BindView(R.id.et_password)
    EditText etPassword;


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_login)
    public void btLoginClick() {
        String login = etEmail.getText().toString();
        String pass = etPassword.getText().toString();

        if(checkFieldsDataCorrect()) {
            RequestViewUtils.showRequest(this);
            LoginLogic loginLogic = new LoginLogic(this);
            loginLogic.login(login, pass, this);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        checkLogged();
    }

    private void checkLogged() {
        boolean logged = LoggedUserRepository.getInstance().load(this);
        if (logged) {
            onLogin();
        }
    }


    @Override
    public void onLogin() {
        RequestViewUtils.hideRequest();

        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        String folder = account.getFolders().getFolderName(FolderType.Inbox);
        
        Intent intent = MailListActivity.makeIntent(this, folder);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFail(ErrorType errorType, int serverCode) {
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this, errorType, serverCode);
    }

    private boolean checkFieldsDataCorrect(){
        if(etEmail.getText().toString().isEmpty()){
            etEmail.setError(getString(R.string.all_email_is_empty));
            etEmail.requestFocus();
            return false;

        }
        else if(!EmailValidator.isValidEmail(etEmail.getText().toString())){
            etEmail.setError(getString(R.string.all_email_is_incorrect));
            etEmail.requestFocus();
            return false;
        } else{
            return true;
        }
    }
}
