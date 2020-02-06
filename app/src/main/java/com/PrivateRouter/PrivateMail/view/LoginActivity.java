package com.PrivateRouter.PrivateMail.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;
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

    @BindView(R.id.et_host)
    EditText etHost;


    @BindView(R.id.et_email)
    EditText etEmail;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.til_host)
    TextInputLayout tilHost;

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_login)
    public void btLoginClick() {
        String login = etEmail.getText().toString();
        String pass = etPassword.getText().toString();
        String host = etHost.getText().toString();

        if (checkFieldsDataCorrect()) {
            RequestViewUtils.showRequest(this);

            LoginLogic loginLogic = new LoginLogic(this);
            loginLogic.login(login, pass, host, this);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        initInputFields();

        checkLogged();
    }

    private void initInputFields() {

        etPassword.setText("");

        SharedPreferences sharedPreferences = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail", "");

        etEmail.setText(email);
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

        SharedPreferences sharedPreferences = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", etEmail.getText().toString());
        editor.apply();

        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        String folder = account.getFolders().getFolderName(FolderType.Inbox);

        Intent intent = MailListActivity.makeIntent(this, folder);
        startActivity(intent);
        finish();
    }

    @Override
    public void requestHost() {
        RequestViewUtils.hideRequest();
        tilHost.setVisibility(View.VISIBLE);
        tilHost.setError(getText(R.string.host_required));
    }

    @Override
    public void onTwoFactorAuth(String login, String pass) {
        Intent intent = new Intent(this, TwoFactorAuthActivity.class);
        intent.putExtra("login", login);
        intent.putExtra("pass", pass);
        startActivity(intent);
    }

    @Override
    public void onFail(ErrorType errorType, String errorString, int serverCode) {
        RequestViewUtils.hideRequest();
        final int needUpgradePlanCode = 108;
        if (errorType == ErrorType.ERROR_REQUEST && serverCode == needUpgradePlanCode) {
            startActivity(new Intent(this, UpgradePlanActivity.class));
        } else
            RequestViewUtils.showError(this, errorType, errorString, serverCode);
    }

    private boolean checkFieldsDataCorrect() {
        String host = etHost.getText().toString();


        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError(getString(R.string.all_email_is_empty));
            etEmail.requestFocus();
            return false;

        } else if (!EmailValidator.isValidEmail(etEmail.getText().toString())) {
            etEmail.setError(getString(R.string.all_email_is_incorrect));
            etEmail.requestFocus();
            return false;

        } else if (tilHost.getVisibility() == View.VISIBLE && host.isEmpty()) {
            etHost.setError(getString(R.string.all_host_is_empty));
            etHost.requestFocus();
            return false;

        } else if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError(getString(R.string.all_password_is_empty));
            etPassword.requestFocus();
            return false;

        } else if (tilHost.getVisibility() == View.VISIBLE && !URLUtil.isValidUrl(host)) {
            etHost.setError(getString(R.string.all_host_is_incorrect));
            etHost.requestFocus();
            return false;
        }

        if (tilHost.getVisibility() == View.VISIBLE) {
            char hostLastCharacter = host.charAt(host.length() - 1);

            if (!Character.toString(hostLastCharacter).equals("/")) {
                StringBuilder stringBuilder = new StringBuilder(host);
                stringBuilder.append("/");
                etHost.setText(stringBuilder);
            }
        }
        return true;
    }
}