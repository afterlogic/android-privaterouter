package com.PrivateRouter.PrivateMail.view.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import com.PrivateRouter.PrivateMail.R;

import com.PrivateRouter.PrivateMail.view.RecreatingActivity;
import com.PrivateRouter.PrivateMail.view.common.StringAdapter;


import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends RecreatingActivity {


    @BindView(R.id.rv_sections)
    RecyclerView rvSections;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initSections();
        setTitle(R.string.all_settings);
    }


    private void initSections() {
        String common = getString(R.string.settings_common);
        String sync = getString(R.string.settings_sync);
        String pgp = getString(R.string.settings_openpgp);
        String [] sections = {common, sync, pgp};

        StringAdapter arrayAdapter = new StringAdapter( this, sections );
        arrayAdapter.setOnItemClick((pos, item) -> {
            if (common.equals(item) ) {
                openCommon();
            }
            else if (sync.equals(item) ) {
                openSync();
            }
            else if (pgp.equals(item) ) {
                openPgp();
            }
        });

        rvSections.setAdapter(arrayAdapter);

    }

    private void openPgp() {
        Intent intent = new Intent(this, PGPSettingsActivity.class);
        startActivity( intent );
    }

    private void openSync() {
        Intent intent = new Intent(this, SyncSettingsActivity.class);
        startActivity( intent );
    }

    private void openCommon() {
        Intent intent = new Intent(this, CommonSettingsActivity.class);
        startActivity( intent );
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
