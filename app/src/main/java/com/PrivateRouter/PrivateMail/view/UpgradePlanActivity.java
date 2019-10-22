package com.PrivateRouter.PrivateMail.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.PrivateRouter.PrivateMail.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.services.network.UrlUtils;

public class UpgradePlanActivity extends AppCompatActivity {


    private static final String UPGRADE_PLAN_LINK = "https://privatemail.com/members/supporttickets.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_plan);
        ButterKnife.bind(this);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

    }


    @SuppressWarnings("unused")
    @OnClick(R.id.bt_upgrade_plan)
    public void btUpgradeClick() {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UPGRADE_PLAN_LINK));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_back_to_login)
    public void btBackClick() {
        finish();
    }
}
