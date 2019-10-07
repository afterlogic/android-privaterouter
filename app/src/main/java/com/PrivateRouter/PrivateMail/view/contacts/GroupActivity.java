package com.PrivateRouter.PrivateMail.view.contacts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
import com.PrivateRouter.PrivateMail.network.requests.CallDeleteGroup;
import com.PrivateRouter.PrivateMail.network.requests.CallLogout;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.network.requests.CallUpdateGroup;
import com.PrivateRouter.PrivateMail.repository.GroupsRepository;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.LoginActivity;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.settings.SettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupActivity extends AppCompatActivity {
    public static final int OPEN_CONTACT = 113;
    public static final int UPDATED_GROUP = 114;
    private Menu menu;
    private Group group;
    private Enum<Mode> modeEnum;

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
    @BindView(R.id.et_group_country)
    TextInputEditText etGroupCountry;
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
            R.id.et_group_country,
            R.id.et_group_state,
            R.id.et_group_city,
            R.id.et_group_street,
            R.id.et_group_zip,
            R.id.et_group_phone,
            R.id.et_group_fax,
            R.id.et_group_web
    })
    List<EditText> etList;

    @BindViews({R.id.et_group_email,
            R.id.et_group_company,
            R.id.et_group_country,
            R.id.et_group_state,
            R.id.et_group_city,
            R.id.et_group_street,
            R.id.et_group_zip,
            R.id.et_group_phone,
            R.id.et_group_fax,
            R.id.et_group_web
    })
    List<EditText> etOrganizationFieldsList;

    @BindViews({R.id.til_group_email,
            R.id.til_group_company,
            R.id.til_group_country,
            R.id.til_group_state,
            R.id.til_group_city,
            R.id.til_group_street,
            R.id.til_group_zip,
            R.id.til_group_phone,
            R.id.til_group_fax,
            R.id.til_group_web
    })
    List<TextInputLayout> tilList;
    //endregion

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, Enum<GroupActivity.Mode> modeEnum, Group group) {
        Intent intent = new Intent(activity, GroupActivity.class);
        intent.putExtra("mode", modeEnum);
        intent.putExtra("group", group);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        group = new Group();
        if (getIntent() != null) {
            Intent intent = getIntent();
            modeEnum = (Enum<Mode>) intent.getExtras().get("mode");
            if (modeEnum.equals(Mode.EDIT) && intent.getExtras().get("group") != null) {
                group = (Group) intent.getExtras().get("group");
                initEditMode(group);
            } else if (modeEnum.equals(Mode.VIEW) && intent.getExtras().get("group") != null) {
                group = (Group) intent.getExtras().get("group");
                initViewMode(group);
            } else if (modeEnum.equals(Mode.CREATE)) {
                initCreateMode();
            }
            intent.getExtras().get("group");
        }

        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATED_GROUP && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        this.menu = menu;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        updateMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_menu_save) {
            RequestViewUtils.showRequest(this);
            saveGroup(collectDataFromFields());
        } else if (id == R.id.item_menu_send) {
            sendMessageToGroup();
        } else if (id == R.id.item_menu_delete) {
            deleteGroup();
        } else if (id == R.id.item_menu_edit) {
            Intent intent = GroupActivity.makeIntent(this, Mode.EDIT, group);
            startActivityForResult(intent, UPDATED_GROUP);
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

    @OnClick(R.id.sw_group_is_a_company)
    public void onSwitchGroupIsACompanyClicked() {
        if (swGroupIsACompany.isChecked()) {
            llGroupIsACompany.setVisibility(View.VISIBLE);
        } else {
            llGroupIsACompany.setVisibility(View.GONE);
        }
    }

    public enum Mode {
        CREATE, VIEW, EDIT
    }

    private void initUI() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        if (modeEnum.equals(Mode.VIEW)) {
            getSupportActionBar().setTitle("");
        }
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);

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
                RequestViewUtils.showError(GroupActivity.this, errorCode, serverCode);
            }
        });
        callLogout.start();
    }

    private void openLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private Group collectDataFromFields() {
        Group group = new Group();
        group.setName(etGroupName.getText().toString());
        group.setIsOrganization(swGroupIsACompany.isChecked());
        group.setEmail(etGroupEmail.getText().toString());
        group.setCompany(etGroupCompany.getText().toString());
        group.setCountry(etGroupCountry.getText().toString());
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
        if (modeEnum.equals(Mode.CREATE)) {
            CallCreateGroup callCreateGroup = new CallCreateGroup(group, new CallRequestResult<String>() {
                @Override
                public void onSuccess(String result) {
                    GroupsRepository.getInstance().load(null, true);
                    RequestViewUtils.hideRequest();

                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFail(ErrorType errorType, int serverCode) {
                    RequestViewUtils.hideRequest();
                    RequestViewUtils.showError(GroupActivity.this, errorType, serverCode);
                }
            });
            callCreateGroup.start();
        } else if (modeEnum.equals(Mode.EDIT)) {
            group.setUUID(this.group.getUUID());
            CallUpdateGroup callUpdateGroup = new CallUpdateGroup(group, new CallRequestResult<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    Intent intent = new Intent();
                    intent.putExtra("group", group);
                    GroupsRepository.getInstance().load(null, true);
                    RequestViewUtils.hideRequest();
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFail(ErrorType errorType, int serverCode) {
                    RequestViewUtils.hideRequest();
                    RequestViewUtils.showError(GroupActivity.this, errorType, serverCode);
                }
            });
            callUpdateGroup.start();
        }
    }

    private void updateMenu() {
        if (menu == null)
            return;

        MenuItem sendItem = menu.findItem(R.id.item_menu_send);
        MenuItem deleteItem = menu.findItem(R.id.item_menu_delete);
        MenuItem editItem = menu.findItem(R.id.item_menu_edit);
        MenuItem saveItem = menu.findItem(R.id.item_menu_save);

        MenuItem actionMail = menu.findItem(R.id.action_mail);
        MenuItem actionContacts = menu.findItem(R.id.action_contacts);
        MenuItem actionSettings = menu.findItem(R.id.action_settings);
        MenuItem actionLogout = menu.findItem(R.id.action_logout);




        if (modeEnum.equals(Mode.VIEW)) {
            saveItem.setVisible(false);
            sendItem.setVisible(true);
            deleteItem.setVisible(true);
            editItem.setVisible(true);

            actionMail.setVisible(true);
            actionContacts.setVisible(true);
            actionSettings.setVisible(true);
            actionLogout.setVisible(true);

        } else if (modeEnum.equals(Mode.EDIT) || modeEnum.equals(Mode.CREATE)) {
            saveItem.setVisible(true);
            sendItem.setVisible(false);
            deleteItem.setVisible(false);
            editItem.setVisible(false);

            actionMail.setVisible(false);
            actionContacts.setVisible(false);
            actionSettings.setVisible(false);
            actionLogout.setVisible(false);
        }
    }

    private void initCreateMode() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        setTitle(getString(R.string.create_group_title));
        group = new Group();
        displayOrganizationFields(false);
        for (EditText et : etList) {
            et.setText("");
        }
    }

    private void initViewMode(Group group) {
        fillGroupFields(group);
        blockFieldsInput();
        blockSwitchState();
        displayOrganizationFields(group.getIsOrganization());
        hideEmptyFields();
    }

    private void initEditMode(Group group) {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        displayOrganizationFields(group.getIsOrganization());
        fillGroupFields(group);
        setTitle(getString(R.string.edit_group_title));
    }

    private void sendMessageToGroup() {

    }

    private void deleteGroup() {
        CallDeleteGroup callDeleteGroup = new CallDeleteGroup(group.getUUID(), new CallRequestResult<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                RequestViewUtils.hideRequest();
                Toast.makeText(GroupActivity.this, "Deleted", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFail(ErrorType errorType, int serverCode) {
                RequestViewUtils.hideRequest();
                RequestViewUtils.showError(GroupActivity.this, errorType, serverCode);
            }
        });
        callDeleteGroup.start();
    }

    private void fillGroupFields(Group group) {
        if (group != null) {
            etGroupName.setText(group.getName());
            swGroupIsACompany.setChecked(group.getIsOrganization());
            etGroupEmail.setText(group.getEmail());
            etGroupCompany.setText(group.getCompany());
            etGroupCountry.setText(group.getCountry());
            etGroupState.setText(group.getState());
            etGroupCity.setText(group.getCity());
            etGroupStreet.setText(group.getStreet());
            etGroupZip.setText(group.getZip());
            etGroupPhone.setText(group.getPhone());
            etGroupFax.setText(group.getFax());
            etGroupWeb.setText(group.getWeb());
        }
    }

    private void blockFieldsInput() {
        int color;
        if (SettingsRepository.getInstance().isNightMode(this))
            color = Color.WHITE;
        else
            color = Color.BLACK;

        for (EditText et : etList) {
            et.setFocusable(false);
            et.setEnabled(false);
            et.setCursorVisible(false);
            et.setKeyListener(null);
            et.setTextColor(color);
        }
    }

    private void blockSwitchState() {
        swGroupIsACompany.setEnabled(false);
    }

    private void displayOrganizationFields(boolean isOrganization) {
        if (!isOrganization) {
            llGroupIsACompany.setVisibility(View.GONE);
        }
    }

    private void hideEmptyFields() {
        swGroupIsACompany.setVisibility(View.GONE);
        int position = 0;
        for (EditText et : etOrganizationFieldsList) {
            if (et.getText().toString().isEmpty()) {
                et.setVisibility(View.GONE);
                tilList.get(position).setVisibility(View.GONE);
            }
            position++;
        }
    }

    private void onGroupOnServerUpdated(Group group) {
        //TO do like in ContactActivity
    }
}
