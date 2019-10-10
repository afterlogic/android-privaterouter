package com.PrivateRouter.PrivateMail.view.settings;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.ExportTask;
import com.PrivateRouter.PrivateMail.encryption.ImportFileTask;
import com.PrivateRouter.PrivateMail.encryption.ImportTask;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.view.common.ActivityWithRequestPermission;
import com.PrivateRouter.PrivateMail.view.common.StringAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.PrivateRouter.PrivateMail.PrivateMailApplication.getContext;


public class PGPSettingsActivity extends ActivityWithRequestPermission {

    private static final int CHOOSE_FILE_CODE = 200;
    @BindView(R.id.rv_private_keys)
    RecyclerView rvPrivateKeys;


    @BindView(R.id.rv_public_keys)
    RecyclerView rvPublicKeys;

    private static final int CODE = 100;

    public static final String IMPORT_KEY_PARAMETER = "KeyParam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_pgp);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 
        setTitle(R.string.settings_openpgp);
        bind();

        if (getIntent()!=null) {
            String keyData = getIntent().getStringExtra(IMPORT_KEY_PARAMETER);
            if (!TextUtils.isEmpty(keyData)) {
                ArrayList<PGPKey> keys = new ImportTask().importKeys(keyData);
                openImportDialog(keys);
            }
        }

    }



    private void bind() {

        ArrayList<PGPKey> privateKeys = PrivateMailApplication.getInstance().getKeysRepository().getPrivateKeys();
        PGPKey[] privateKeyArr = new PGPKey[privateKeys .size() ];
        privateKeyArr  = privateKeys.toArray(privateKeyArr);
        StringAdapter privateKeyAdapter = new StringAdapter( this,  privateKeyArr  );
        privateKeyAdapter.setOnItemClick((pos, item) -> {
            Intent intent = PGPKeyActivity.makeIntent(PGPSettingsActivity.this, (PGPKey) item);
            startActivityForResult(intent, CODE);
        });
        rvPrivateKeys.setAdapter(privateKeyAdapter);


        ArrayList<PGPKey> publicKeys =  PrivateMailApplication.getInstance().getKeysRepository().getPublicKeys();
        PGPKey[] publicKeysArr = new PGPKey[publicKeys.size() ];
        publicKeysArr  = publicKeys.toArray(publicKeysArr);
        StringAdapter publicKeyAdapter = new StringAdapter( this,  publicKeysArr  );
        publicKeyAdapter.setOnItemClick((pos, item) -> {
            Intent intent = PGPKeyActivity.makeIntent(PGPSettingsActivity.this, (PGPKey) item);
            startActivityForResult(intent, CODE);
        });
        rvPublicKeys.setAdapter(publicKeyAdapter);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_export_all_public_keys)
    public void btExportAllClick() {
        Intent intent = PGPKeysActivity.makeIntent(this);
        startActivity(intent);

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_import_keys_text)
    public void btImportTextClick() {
        ImportPGPKeyDialogFragment dialogFragment = new ImportPGPKeyDialogFragment();
        dialogFragment.setOnImportRunnable(this::bind);
        dialogFragment.show(getSupportFragmentManager(), "importKeyDialog");
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_import_keys_file)
    public void btImportFileClick() {
        boolean havePermission =  checkAndRequest(Manifest.permission.READ_EXTERNAL_STORAGE, this::btImportFileClick);

        if (havePermission) {
            Intent intent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, getString(R.string.settings_select_key_file)), CHOOSE_FILE_CODE);
        }
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_generate_keys)
    public void btGenerateClick() {
        PGPKeyGenerateDialogFragment dialogFragment = new PGPKeyGenerateDialogFragment();
        dialogFragment.setOnGenerateRunnable(this::bind);
        dialogFragment.show(getSupportFragmentManager(), "generateKeyDialog");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE) {
            bind();
        }
        else if(requestCode == CHOOSE_FILE_CODE && resultCode == RESULT_OK) {
            Uri selectedFile = data.getData();

            String fileName = selectedFile.getPath();
            String ext = fileName.substring(fileName.length()-3 );
            if (!"asc".equalsIgnoreCase(ext)) {
                Toast.makeText(this, getString(R.string.settings_key_file_not_valid), Toast.LENGTH_LONG).show();
                return;
            }

            ImportFileTask importFileTask = new ImportFileTask(selectedFile);
            importFileTask.setRunnableOnFinish(new ImportFileTask.OnImportFile() {
                @Override
                public void onImportFile(ArrayList<PGPKey> keys) {
                    openImportDialog(keys);
                }
            });
            importFileTask.execute();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openImportDialog(ArrayList<PGPKey> keys) {
        ImportPGPKeyDialogFragment dialogFragment = new ImportPGPKeyDialogFragment();
        dialogFragment.setPgpKeyList(keys );
        Bundle bundle = new Bundle();
        bundle.putBoolean(ImportPGPKeyDialogFragment.CheckMode, true);
        dialogFragment.setArguments(bundle);

        dialogFragment.setOnImportRunnable(() -> {
            bind();
        });
        dialogFragment.show(PGPSettingsActivity.this.getSupportFragmentManager(), "importKeyCheckDialog");
    }

}