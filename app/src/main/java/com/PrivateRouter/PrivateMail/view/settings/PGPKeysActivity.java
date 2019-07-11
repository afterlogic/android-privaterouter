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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PGPKeysActivity  extends ActivityWithRequestPermission {


    @BindView(R.id.tv_key_data)
    TextView tvKeyData;


    String pgpKeyData;
    public static final String PARAM = "PgpKey";

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity ) {
        Intent intent = new Intent(activity, PGPKeysActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pgp_keys );

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.settings_all_public_keys);

        ArrayList<PGPKey> pgpKeys = PrivateMailApplication.getInstance().getKeysRepository().getPublicKeys();

        pgpKeyData = "";
        for (int i = 0; i< pgpKeys.size(); i++) {
            PGPKey pgpKey = pgpKeys.get(i);
            pgpKeyData = pgpKeyData + pgpKey.getKeyObject().toString() +"\n";
        }

        tvKeyData.setText( pgpKeyData );

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
    @OnClick(R.id.bt_send_all)
    public void btSendClick() {


        Message message = new Message();
        message.setPlain( pgpKeyData );
        Intent intent = ComposeActivity.makeIntent(this, message );
        startActivity(intent);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_download_all)
    public void btDownloadClick() {
        boolean havePermission =  checkAndRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, this::btDownloadClick);

        if (havePermission) {
            ExportTask exportTask = new ExportTask("OpenPGP public keys.asc");
            exportTask.setRunnableOnFinish(() -> Toast.makeText(this, getString(R.string.settings_export_complete), Toast.LENGTH_LONG).show());

            exportTask.execute( pgpKeyData );

        }

    }



}