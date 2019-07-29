package com.PrivateRouter.PrivateMail.view.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallCreateGroup;
import com.PrivateRouter.PrivateMail.network.requests.CallLogout;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.LoginActivity;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.settings.SettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateContactsGroupActivity extends AppCompatActivity {

    //region Butterknife binds
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_group_name)
    TextInputEditText etGroupName;
    @BindView(R.id.sw_group_is_a_company)
    Switch swGroupIsACompany;
    @BindView(R.id.et_group_email)
    TextInputEditText etGroupEmail;
    @BindView(R.id.et_group_company)
    TextInputEditText etGroupCompany;
    @BindView(R.id.et_group_state)
    TextInputEditText etGroupState;
    @BindView(R.id.et_group_city)
    TextInputEditText etGroupCity;
    @BindView(R.id.et_group_street)
    TextInputEditText etGroupStreet;
    @BindView(R.id.et_group_zip)
    TextInputEditText etGroupZip;
    @BindView(R.id.et_group_phone)
    TextInputEditText etGroupPhone;
    @BindView(R.id.et_group_fax)
    TextInputEditText etGroupFax;
    @BindView(R.id.et_group_web)
    TextInputEditText etGroupWeb;
    @BindView(R.id.ll_group_is_a_company)
    LinearLayout llGroupIsACompany;

    @BindViews({R.id.et_group_name,
            R.id.et_group_email,
            R.id.et_group_company,
            R.id.et_group_state,
            R.id.et_group_city,
            R.id.et_group_street,
            R.id.et_group_zip,
            R.id.et_group_phone,
            R.id.et_group_fax,
            R.id.et_group_web
    })
    List<EditText> etList;
    //endregion

    public static final int OPEN_CONTACT = 1012;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contacts_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_contacts_group_menu, menu);
        this.menu = menu;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_menu_save) {
            RequestViewUtils.showRequest(this);
            saveGroup(collectDataFromFields());
        } else if (id == R.id.action_mail) {
            Account account = LoggedUserRepository.getInstance().getActiveAccount();
            String folder = account.getFolders().getFolderName(FolderType.Inbox);
            Intent intent = MailListActivity.makeIntent(this, folder);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_contacts) {
            Intent intent = ContactsActivity.makeIntent(this, false);
            startActivityForResult(intent, OPEN_CONTACT);
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.sw_group_is_a_company)
    public void onSwitchGroupIsACompanyClicked() {
        if (swGroupIsACompany.isChecked()) {
            llGroupIsACompany.setVisibility(View.VISIBLE);
        } else {
            llGroupIsACompany.setVisibility(View.GONE);
        }
    }

    private void initUI() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        for (EditText et : etList) {
            et.setText("");
        }
        llGroupIsACompany.setVisibility(View.GONE);
    }

    private void logout() {
        LoggedUserRepository.getInstance().logout(this);
        RequestViewUtils.showRequest(this);
        CallLogout callLogout = new CallLogout(new CallRequestResult() {
            @Override
            public void onSuccess(Object result) {
                RequestViewUtils.hideRequest();
                openLoginScreen();
                finish();
            }

            @Override
            public void onFail(ErrorType errorCode, int serverCode) {
                RequestViewUtils.hideRequest();
                RequestViewUtils.showError(CreateContactsGroupActivity.this, errorCode, serverCode);
            }
        });
        callLogout.start();
    }

    private void openLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private Group collectDataFromFields(){
        Group group = new Group();
        group.setName(etGroupName.getText().toString());
        group.setIsOrganization(swGroupIsACompany.isChecked() ? 1 : 0);
        group.setEmail(etGroupEmail.getText().toString());
        group.setCompany(etGroupCompany.getText().toString());
        group.setState(etGroupState.getText().toString());
        group.setCity(etGroupCity.getText().toString());
        group.setStreet(etGroupStreet.getText().toString());
        group.setZip(etGroupZip.getText().toString());
        group.setPhone(etGroupPhone.getText().toString());
        group.setFax(etGroupFax.getText().toString());
        group.setWeb(etGroupWeb.getText().toString());
        return group;
    }

    private void saveGroup(Group group) {
        CallCreateGroup callCreateGroup = new CallCreateGroup(group, new CallRequestResult<String>() {
            @Override
            public void onSuccess(String result) {
                RequestViewUtils.hideRequest();
                Toast.makeText(CreateContactsGroupActivity.this, result, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFail(ErrorType errorType, int serverCode) {
                RequestViewUtils.hideRequest();
                RequestViewUtils.showError(CreateContactsGroupActivity.this, errorType, serverCode);
            }
        });
        callCreateGroup.start();
    }
}
