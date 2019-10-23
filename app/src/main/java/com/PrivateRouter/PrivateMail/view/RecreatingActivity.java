package com.PrivateRouter.PrivateMail.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.PrivateRouter.PrivateMail.view.settings.CommonSettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.Logger;

public class RecreatingActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    protected boolean recreateAfterResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBroadcastReceiver();
    }

    protected void onResume() {
        super.onResume();
        if (recreateAfterResume) {
            recreateAfterResume = false;
            recreate();
        }

    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                recreateAfterResume = true;
            }
        };
        registerReceiver(broadcastReceiver,  new IntentFilter(CommonSettingsActivity.THEME_CHANGE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null)
        {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }
}
