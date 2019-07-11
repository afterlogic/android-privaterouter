package com.PrivateRouter.PrivateMail.view.settings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.FolderHash;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.components.SettingTextView;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SyncSettingsActivity extends AppCompatActivity {

    @BindView(R.id.sv_sync_frequency)
    SettingTextView svSyncFrequency;

    @BindView(R.id.sv_sync_period)
    SettingTextView svSyncPeriod;


    boolean programSelect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_settings);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings_sync);

        initSyncPeriod();
        initSyncFrequency();

    }

    private void initSyncPeriod() {

        String[] data = getResources().getStringArray(R.array.settings_sync_period_values);
        Context context =  this;
        svSyncPeriod.setVariants(data);
        int format = SettingsRepository.getInstance().getSyncPeriod(this);
        svSyncPeriod.setSelectedVariant(format);
        svSyncPeriod.setOnSelectInterface((position, value) -> {
            SettingsRepository.getInstance().setSyncPeriod(context, position);
            clearFolderHash();
            PrivateMailApplication.getInstance().getSyncLogic().updateTimer();


        });

    }

    private void clearFolderHash() {
         AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
                database.messageDao().deletedFolderHashes();
                return null;
            }
        };
        asyncTask.execute();

    }


    private void initSyncFrequency() {

        String[] data = getResources().getStringArray(R.array.settings_sync_frequency_values);
        Context context =  this;
        svSyncFrequency.setVariants(data);
        int format = SettingsRepository.getInstance().getSyncFrequency(this);
        svSyncFrequency.setSelectedVariant(format);
        svSyncFrequency.setOnSelectInterface((position, value) -> {
            SettingsRepository.getInstance().setSyncFrequency(context, position);
        });



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
