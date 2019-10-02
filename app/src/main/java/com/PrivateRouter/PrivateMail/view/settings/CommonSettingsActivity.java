package com.PrivateRouter.PrivateMail.view.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
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
import com.PrivateRouter.PrivateMail.view.components.SettingSwitch;
import com.PrivateRouter.PrivateMail.view.components.SettingTextView;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonSettingsActivity extends AppCompatActivity {


    public static final String THEME_CHANGE = "THEME_CHANGE";
    @BindView(R.id.sv_time_format)
    SettingTextView svTimeFormat;

    @BindView(R.id.sv_night_mode)
    SettingSwitch svNightMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_settings);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initTimeFormat();

        initNightMode();
        setTitle(R.string.settings_common);
    }

    private void initNightMode() {

        svNightMode.setCheck( SettingsRepository.getInstance().isNightMode(this) );
        svNightMode.setOnValueChangeCallback(new SettingSwitch.OnValueChangeCallback() {
            @Override
            public void onValueChangeCallback(boolean value) {
                SettingsRepository.getInstance().setNightMode(CommonSettingsActivity.this, value);

                if (value) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                sendBroadcast();
                updateConfig();
            }
        });
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(THEME_CHANGE);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    private void updateConfig() {
        recreate();
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
