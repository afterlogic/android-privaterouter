package com.PrivateRouter.PrivateMail.view.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.ImportTask;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.view.utils.Utils;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.pgpainless.PGPainless;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportPGPKeyDialogFragment extends DialogFragment implements View.OnClickListener, PGPKeyViewHolder.OnChecked  {

    public static String CheckMode = "CheckMode";

    @BindView(R.id.ll_check)
    View llCheck;

    @BindView(R.id.ll_import)
    View llImport;

    @BindView(R.id.et_key_data)
    EditText etKeyData;

    @BindView(R.id.rv_import_keys)
    RecyclerView rvImportKeys;

    @BindView(R.id.bt_import_checked_keys)
    Button btImportKeys;

    private List<PGPKey> pgpKeyList;
    private LinkedList<PGPKey> pgpKeyListSelected = new LinkedList<>();


    private Runnable onImportRunnable;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_import_key, null);
        ButterKnife.bind(this, view);

        etKeyData.setText("");

        if (getArguments()!=null) {
            boolean checkMode = getArguments().getBoolean(CheckMode);
            if (checkMode)
                changeToCheckMode();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.dialog_relative_width, outValue, true);
        float relativeWidth = outValue.getFloat();

        int width = (int)(Utils.getDeviceMetrics(getActivity()).widthPixels * relativeWidth);
        int height = (int)(Utils.getDeviceMetrics(getActivity()).heightPixels* relativeWidth);
        getDialog().getWindow().setLayout(  width,  height);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_import_keys)
    public void btImportKeysClick() {
        pgpKeyList =  new ImportTask().importKeys( etKeyData.getText().toString() );
        changeToCheckMode();
    }

    public void changeToCheckMode() {
        llCheck.setVisibility(View.VISIBLE);
        llImport.setVisibility(View.GONE);

        Context context = getActivity();
        PGPKeyAdapter pgpKeyAdapter = new PGPKeyAdapter(context, pgpKeyList);
        pgpKeyAdapter.setOnChecked(this);
        rvImportKeys.setAdapter(pgpKeyAdapter);

        btImportKeys.setEnabled(!pgpKeyList.isEmpty());
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_import_checked_keys)
    public void btImportCheckKeysClick() {

        PrivateMailApplication.getInstance().getKeysRepository().addKeys( pgpKeyListSelected );
        if (onImportRunnable !=null)
            onImportRunnable.run();
        dismiss();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_close)
    public void btCloseClick() {
        dismiss();
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onChecked(PGPKey pgpKey, boolean checked) {
        pgpKeyListSelected.remove(pgpKey);

        if (checked)
            pgpKeyListSelected.addFirst(pgpKey);
    }



    public Runnable getOnImportRunnable() {
        return onImportRunnable;
    }

    public void setOnImportRunnable(Runnable onImportRunnable) {
        this.onImportRunnable = onImportRunnable;
    }

    public List<PGPKey> getPgpKeyList() {
        return pgpKeyList;
    }

    public void setPgpKeyList(List<PGPKey> pgpKeyList) {
        this.pgpKeyList = pgpKeyList;
    }
}
