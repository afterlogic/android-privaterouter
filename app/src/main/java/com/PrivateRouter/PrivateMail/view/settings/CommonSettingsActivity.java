package com.PrivateRouter.PrivateMail.view.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.components.SettingTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonSettingsActivity extends AppCompatActivity {


    @BindView(R.id.sv_time_format)
    SettingTextView svTimeFormat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_settings);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initTimeFormat();
        setTitle(R.string.settings_common);
    }

    private void initTimeFormat() {

        String[] data = { "1PM", "13:00" };


        svTimeFormat.setVariants(data);
        boolean time24Format = SettingsRepository.getInstance().isTime24Format(this);
        if (time24Format)
            svTimeFormat.setSelectedVariant(1);
        else
            svTimeFormat.setSelectedVariant(0);


        svTimeFormat.setOnSelectInterface((position, value) -> {
            boolean pmFormat = position==0;
            SettingsRepository.getInstance().setTimeFormat(CommonSettingsActivity.this, pmFormat );
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
