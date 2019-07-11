package com.PrivateRouter.PrivateMail.view;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputEmailDialogFragment  extends DialogFragment  {

    public OnEmailInput getOnEmailInput() {
        return onEmailInput;
    }

    public void setOnEmailInput(OnEmailInput onEmailInput) {
        this.onEmailInput = onEmailInput;
    }

    public interface OnEmailInput{
        void onEmailInput(String email);
    }

    private OnEmailInput onEmailInput;

    @BindView(R.id.et_input_email)
    EditText etEmail;



    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_input_email, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_input_ok)
    public void btOkClick() {
        String email = etEmail.getText().toString();
        if (EmailValidator.isValidEmail(email)) {
            if (onEmailInput != null)
                onEmailInput.onEmailInput(email);
            dismiss();
        }
        else {
            Toast.makeText(getActivity(), getString(R.string.dialog_not_valid_email), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_input_cancel)
    public void btCancelClick() {
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

}
