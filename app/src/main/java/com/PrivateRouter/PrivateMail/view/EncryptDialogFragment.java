package com.PrivateRouter.PrivateMail.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.DecryptCallback;
import com.PrivateRouter.PrivateMail.encryption.DecryptTask;
import com.PrivateRouter.PrivateMail.encryption.EncryptCallback;
import com.PrivateRouter.PrivateMail.encryption.EncryptTask;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.utils.MessageUtils;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;
import com.PrivateRouter.PrivateMail.view.utils.Utils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EncryptDialogFragment extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.tv_encrypt_dialog_title)
    TextView tvTitle;

    @BindView(R.id.cv_sign_divider)
    View cvSignDivider;

    @BindView(R.id.cv_encrypt_divider)
    View cvEncryptDivider;

    @BindView(R.id.cb_encrypt_dialog_sign)
    CheckBox cbSign;

    @BindView(R.id.cb_encrypt_dialog_encrypt)
    CheckBox cbEncrypt;

    @BindView(R.id.et_encrypt_password)
    EditText etPassword;

    @BindView(R.id.bt_decrypt)
    Button btDecrypt;

    @BindView(R.id.bt_encrypt)
    Button btEncrypt;



    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


    private EncryptCallback encryptCallback;
    private DecryptCallback decryptCallback;

    private Message message;



    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_sign_encrypt, null);
        ButterKnife.bind(this, view);

        updateUI();

        return view;
    }

    private void updateUI() {
        btDecrypt.setVisibility(View.GONE);
        btEncrypt.setVisibility(View.GONE);
        if (message!=null) {

            if (MessageUtils.isEncrypted(message)) {
                btDecrypt.setVisibility(View.VISIBLE);
                tvTitle.setText(R.string.encrypt_dialog_title_decrypt);
                cvEncryptDivider.setVisibility(View.GONE);
                cvSignDivider.setVisibility(View.GONE);
                cbEncrypt.setVisibility(View.GONE);
                cbSign.setVisibility(View.GONE);

            }
            else {
                btEncrypt.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_encrypt_cancel)
    public void btCancelClick() {
        dismiss();
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_decrypt)
    public void btDecrypt() {
        String pass = etPassword.getText().toString();


        RequestViewUtils.showRequest(getActivity());
        DecryptTask encryptTask = new DecryptTask(getActivity(), pass, message, new DecryptCallback() {
            @Override
            public void onDecrypt(Message message) {
                RequestViewUtils.hideRequest();
                if (decryptCallback!=null)
                    decryptCallback.onDecrypt(message);
                dismiss();
            }

            @Override
            public void onFail(String description) {
                onFailOperation(description);
            }
        });
        encryptTask.execute();

    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_encrypt)
    public void btEncrypt() {
        String pass = etPassword.getText().toString();


        RequestViewUtils.showRequest(getActivity());
        EncryptTask encryptTask = new EncryptTask(getActivity(), pass, cbSign.isChecked(), message,
                new EncryptCallback() {
                    @Override
                    public void onEncrypt(Message message) {
                        RequestViewUtils.hideRequest();
                        if (encryptCallback != null)
                            encryptCallback.onEncrypt(message);
                        dismiss();
                    }

                    @Override
                    public void onFail(String description) {
                        onFailOperation(description);
                    }
                } );
        encryptTask.execute();

    }

    private void onFailOperation(String description) {

        RequestViewUtils.hideRequest();
        dismiss();
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.app_name))
                .setMessage(description )
                .setPositiveButton(R.string.all_ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();


        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.dialog_relative_width, outValue, true);
        float relativeWidth = outValue.getFloat();

        int width = (int)(Utils.getDeviceMetrics(getActivity()).widthPixels * relativeWidth);
        getDialog().getWindow().setLayout(  width,  ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public EncryptCallback getEncryptCallback() {
        return encryptCallback;
    }

    public void setEncryptCallback(EncryptCallback encryptCallback) {
        this.encryptCallback = encryptCallback;
    }

    public DecryptCallback getDecryptCallback() {
        return decryptCallback;
    }

    public void setDecryptCallback(DecryptCallback decryptCallback) {
        this.decryptCallback = decryptCallback;
    }
}
