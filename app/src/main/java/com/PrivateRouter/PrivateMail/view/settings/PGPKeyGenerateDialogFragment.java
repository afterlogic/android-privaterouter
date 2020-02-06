package com.PrivateRouter.PrivateMail.view.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.GenerateTask;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.repository.KeysRepository;
import com.PrivateRouter.PrivateMail.view.utils.EmailUtils;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;
import com.PrivateRouter.PrivateMail.view.utils.Utils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PGPKeyGenerateDialogFragment extends DialogFragment {

    @BindView(R.id.sp_strength)
    Spinner spStrength;

    @BindView(R.id.et_encrypt_password)
    EditText etEncryptPassword;

    @BindView(R.id.sp_mail)
    EditText spMail;

    private Runnable onGenerateRunnable;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_generate_key, null);
        ButterKnife.bind(this, view);

        initUI();
        return view;
    }

    private void initUI() {

        initMail();
        initStrength();

    }

    private void initMail() {
        Account account = PrivateMailApplication.getInstance().getLoggedUserRepository().getActiveAccount();

        spMail.setText(account.getEmail());
    }

    private void initStrength() {
        String[] data = {"1024", "2048", "3072", "4096", "8192",};
        Context context = getActivity();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStrength.setAdapter(adapter);
        spStrength.setSelection(1);
    }


    @Override
    public void onResume() {
        super.onResume();


        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.dialog_relative_width, outValue, true);
        float relativeWidth = outValue.getFloat();

        int width = (int) (Utils.getDeviceMetrics(getActivity()).widthPixels * relativeWidth);
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_generate)
    public void btGenerateClick() {

        String mail = spMail.getText().toString();


        String userId = EmailUtils.encapsulationEmail(mail);
        String strength = (String) spStrength.getSelectedItem();
        String pass = etEncryptPassword.getText().toString();
        ;

        KeysRepository repository = PrivateMailApplication.getInstance().getKeysRepository();
        if (repository.getKey(userId, "") != null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getActivity().getString(R.string.generate_dialog_have_key))
                    .setPositiveButton(R.string.all_ok, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        } else {

            RequestViewUtils.showRequest(getActivity());
            GenerateTask generateTask = new GenerateTask(userId, strength, pass);
            generateTask.setRunnableOnFinish(this::onComplete);
            generateTask.execute();
        }

    }

    private void onComplete() {
        RequestViewUtils.hideRequest();

        if (onGenerateRunnable != null)
            onGenerateRunnable.run();

        dismiss();
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_cancel)
    public void btCloseClick() {
        dismiss();
    }


    public Runnable getOnGenerateRunnable() {
        return onGenerateRunnable;
    }

    public void setOnGenerateRunnable(Runnable onGenerateRunnable) {
        this.onGenerateRunnable = onGenerateRunnable;
    }
}
