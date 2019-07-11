package com.PrivateRouter.PrivateMail.view.settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.ExportTask;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.view.ComposeActivity;
import com.PrivateRouter.PrivateMail.view.common.ActivityWithRequestPermission;
import com.PrivateRouter.PrivateMail.view.utils.EmailUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PGPKeyActivity extends ActivityWithRequestPermission {


    @BindView(R.id.tv_key_data)
    TextView tvKeyData;

    @BindView(R.id.tv_key_id)
    TextView tvKeyId;

    PGPKey pgpKey;
    public static final String PARAM = "PgpKey";

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, PGPKey pgpKey) {
        Intent intent = new Intent(activity, PGPKeyActivity.class);
        intent.putExtra(PARAM, pgpKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pgp_key );

        ButterKnife.bind(this);
        setTitle(R.string.settings_openpgp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent()!=null ) {
            pgpKey = (PGPKey) getIntent().getSerializableExtra(PARAM);

            if (pgpKey!=null) {
                if (PGPKey.PRIVATE.equalsIgnoreCase(pgpKey.getType()) )
                    setTitle(R.string.settings_private_key);
                else
                    setTitle(R.string.settings_public_key);
                bind();
            }

        }
        else {
            finish();
        }

    }
    private void bind() {
        tvKeyData.setText( pgpKey.getKeyObject().toString() );
        tvKeyId.setText( pgpKey.getUserID() );
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }




    @SuppressWarnings("unused")
    @OnClick(R.id.bt_send)
    public void btSendClick() {
        Message message = new Message();
        message.setPlain( pgpKey.getKeyObject().toString()  );
        Intent intent = ComposeActivity.makeIntent(this, message );
        startActivity(intent);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_download)
    public void btDownloadClick() {
        boolean havePermission = checkAndRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, this::btDownloadClick);

        if (havePermission) {
            String fileName = EmailUtils.decapsulationEmail( pgpKey.getUserID()) + " OpenPGP "+pgpKey.getType() +"keys.asc";
            ExportTask exportTask = new ExportTask(fileName);
            exportTask.setRunnableOnFinish(() -> Toast.makeText(this, getString(R.string.settings_export_complete), Toast.LENGTH_LONG).show());
            exportTask.execute(pgpKey.getKeyObject().toString());

        }
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_delete)
    public void btDeleteClick() {

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.settings_confirm_dialog) )
                .setPositiveButton( R.string.all_ok, (dialog, which) -> {
                    PrivateMailApplication.getInstance().getKeysRepository().removeKey(pgpKey);
                    finish();
                })
                .setNegativeButton(R.string.all_cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();


    }

}