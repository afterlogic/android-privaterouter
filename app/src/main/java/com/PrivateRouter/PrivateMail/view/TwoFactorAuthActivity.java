package com.PrivateRouter.PrivateMail.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.widget.EditText;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.logics.PinVerifyLogic;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TwoFactorAuthActivity extends AppCompatActivity implements PinVerifyLogic.OnVerifyCallback {

    String login;
    String pass;


    @BindView(R.id.et_pin)
    EditText etPin;

    @BindView(R.id.til_pin)
    TextInputLayout tilPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_auth);
        ButterKnife.bind(this);
        login = getIntent().getStringExtra("login");
        pass = getIntent().getStringExtra("pass");

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    @OnClick(R.id.bt_verify)
    public void btVerify() {
        if (checkFieldsDataCorrect()) {
            String pin = etPin.getText().toString();
            RequestViewUtils.showRequest(this);

            PinVerifyLogic pinVerifyLogic = new PinVerifyLogic(this, login, pass);
            pinVerifyLogic.verify(pin, this);
        }
    }

    private boolean checkFieldsDataCorrect() {
        String pin = etPin.getText().toString();
        if (pin.isEmpty()) {
            tilPin.setError(getString(R.string.all_is_empty));
            tilPin.requestFocus();
            return false;

        }
        return true;
    }

    @OnClick(R.id.bt_cancel)
    public void btCancel() {
        RequestViewUtils.hideRequest();
        finish();
    }

    @Override
    public void onLogin() {
        RequestViewUtils.hideRequest();

        SharedPreferences sharedPreferences = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", login);
        editor.apply();

        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        String folder = account.getFolders().getFolderName(FolderType.Inbox);

        Intent intent = MailListActivity.makeIntent(this, folder);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFail(ErrorType errorType, String errorString, int serverCode) {
        RequestViewUtils.hideRequest();
        final int needUpgradePlanCode = 108;
        if (errorType == ErrorType.ERROR_REQUEST) {
            RequestViewUtils.showError(this, errorType, getString(R.string.invalid_pin), serverCode);
        } else
            RequestViewUtils.showError(this, errorType, errorString, serverCode);
    }
}
