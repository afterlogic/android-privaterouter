package com.PrivateRouter.PrivateMail.view.contacts;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.ContactSettings;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.EmailCollection;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallCreateContact;
import com.PrivateRouter.PrivateMail.network.requests.CallLogout;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.network.requests.CallUpdateContact;
import com.PrivateRouter.PrivateMail.network.responses.UpdateContactResponse;
import com.PrivateRouter.PrivateMail.repository.ContactSettingsRepository;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.ComposeActivity;
import com.PrivateRouter.PrivateMail.view.LoginActivity;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.settings.SettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactActivity extends AppCompatActivity implements ContactSettingsRepository.OnContactLoadCallback {

    private ContactSettings contactSettings;
    private Menu menu;
    private Enum<Mode> modeEnum;
    private Contact contact;

    public static final int OPEN_CONTACT = 1011;

    //region Butterknife binds
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_display_name)
    TextInputEditText etDisplayName;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.sp_email)
    Spinner spEmail;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.sp_phone)
    Spinner spPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.sp_address)
    Spinner spAddress;
    @BindView(R.id.et_skype)
    TextInputEditText etSkype;
    @BindView(R.id.et_facebook)
    TextInputEditText etFacebook;
    @BindView(R.id.tv_additional_fields)
    TextView tvAdditionalFields;
    @BindView(R.id.et_additional_first_name)
    TextInputEditText etAdditionalFirstName;
    @BindView(R.id.et_additional_last_name)
    TextInputEditText etAdditionalLastName;
    @BindView(R.id.et_additional_nickname)
    TextInputEditText etAdditionalNickname;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.et_home_personal_e_mail)
    TextInputEditText etHomePersonalEMail;
    @BindView(R.id.et_home_street_address)
    TextInputEditText etHomeStreetAddress;
    @BindView(R.id.et_home_city)
    TextInputEditText etHomeCity;
    @BindView(R.id.et_home_state_province)
    TextInputEditText etHomeStateProvince;
    @BindView(R.id.et_home_zip_code)
    TextInputEditText etHomeZipCode;
    @BindView(R.id.et_home_country_region)
    TextInputEditText etHomeCountryRegion;
    @BindView(R.id.et_home_web_page)
    TextInputEditText etHomeWebPage;
    @BindView(R.id.et_home_fax)
    TextInputEditText etHomeFax;
    @BindView(R.id.et_home_phone)
    TextInputEditText etHomePhone;
    @BindView(R.id.et_home_mobile)
    TextInputEditText etHomeMobile;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.tv_business)
    TextView tvBusiness;
    @BindView(R.id.et_business_e_mail)
    TextInputEditText etBusinessEMail;
    @BindView(R.id.et_business_company)
    TextInputEditText etBusinessCompany;
    @BindView(R.id.et_business_department)
    TextInputEditText etBusinessDepartment;
    @BindView(R.id.et_business_job_title)
    TextInputEditText etBusinessJobTitle;
    @BindView(R.id.et_business_office)
    TextInputEditText etBusinessOffice;
    @BindView(R.id.et_business_street_address)
    TextInputEditText etBusinessStreetAddress;
    @BindView(R.id.et_business_city)
    TextInputEditText etBusinessCity;
    @BindView(R.id.et_business_state_province)
    TextInputEditText etBusinessStateProvince;
    @BindView(R.id.et_business_zip_code)
    TextInputEditText etBusinessZipCode;
    @BindView(R.id.et_business_country_region)
    TextInputEditText etBusinessCountryRegion;
    @BindView(R.id.et_business_web_page)
    TextInputEditText etBusinessWebPage;
    @BindView(R.id.et_business_fax)
    TextInputEditText etBusinessFax;
    @BindView(R.id.et_business_phone)
    TextInputEditText etBusinessPhone;
    @BindView(R.id.ll_business)
    LinearLayout llBusiness;
    @BindView(R.id.tv_other)
    TextView tvOther;
    @BindView(R.id.et_other_birthday)
    TextInputEditText etOtherBirthday;
    @BindView(R.id.et_other_e_mail)
    TextInputEditText etOtherEMail;
    @BindView(R.id.et_other_notes)
    TextInputEditText etOtherNotes;
    @BindView(R.id.ll_other)
    LinearLayout llOther;
    @BindView(R.id.ll_additional_fields)
    LinearLayout llAdditionalFields;

    @BindViews({R.id.et_display_name,
            R.id.et_skype,
            R.id.et_facebook,
            R.id.et_additional_first_name,
            R.id.et_additional_last_name,
            R.id.et_additional_nickname,
            R.id.et_home_personal_e_mail,
            R.id.et_home_street_address,
            R.id.et_home_city,
            R.id.et_home_state_province,
            R.id.et_home_zip_code,
            R.id.et_home_country_region,
            R.id.et_home_web_page,
            R.id.et_home_fax,
            R.id.et_home_phone,
            R.id.et_home_mobile,
            R.id.et_business_e_mail,
            R.id.et_business_company,
            R.id.et_business_department,
            R.id.et_business_job_title,
            R.id.et_business_office,
            R.id.et_business_street_address,
            R.id.et_business_city,
            R.id.et_business_state_province,
            R.id.et_business_zip_code,
            R.id.et_business_country_region,
            R.id.et_business_web_page,
            R.id.et_business_fax,
            R.id.et_business_phone,
            R.id.et_other_birthday,
            R.id.et_other_e_mail,
            R.id.et_other_notes
    })
    List<EditText> etList;

    @BindViews({R.id.sp_email,
            R.id.sp_phone,
            R.id.sp_address
    })
    List<Spinner> spList;

    @BindViews({R.id.til_display_name,
            R.id.til_skype,
            R.id.til_facebook,
            R.id.til_additional_first_name,
            R.id.til_additional_last_name,
            R.id.til_additional_nickname,
            R.id.til_home_personal_e_mail,
            R.id.til_home_street_address,
            R.id.til_home_state_province,
            R.id.til_home_zip_code,
            R.id.til_home_city,
            R.id.til_home_country_region,
            R.id.til_home_web_page,
            R.id.til_home_fax,
            R.id.til_home_phone,
            R.id.til_home_mobile,
            R.id.til_business_e_mail,
            R.id.til_business_company,
            R.id.til_business_department,
            R.id.til_business_job_title,
            R.id.til_business_office,
            R.id.til_business_street_address,
            R.id.til_business_city,
            R.id.til_business_state_province,
            R.id.til_business_zip_code,
            R.id.til_business_country_region,
            R.id.til_business_web_page,
            R.id.til_business_fax,
            R.id.til_business_phone,
            R.id.til_other_birthday,
            R.id.til_other_e_mail,
            R.id.til_other_notes})
    List<TextInputLayout> tilList;
    //endregion

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, Enum<ContactActivity.Mode> modeEnum, Contact contact) {
        Intent intent = new Intent(activity, ContactActivity.class);
        intent.putExtra("mode", modeEnum);
        intent.putExtra("contact", contact);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        contact = null;
        if (getIntent() != null) {
            Intent intent = getIntent();
            modeEnum = (Enum<Mode>) intent.getExtras().get("mode");
            if (modeEnum.equals(Mode.EDIT) && intent.getExtras().get("contact") != null) {
                contact = (Contact) intent.getExtras().get("contact");
                initEditMode(contact);
            } else if (modeEnum.equals(Mode.VIEW) && intent.getExtras().get("contact") != null) {
                contact = (Contact) intent.getExtras().get("contact");
                initViewMode(contact);
            } else {
                initCreateMode();
            }
            intent.getExtras().get("contact");
        }
        initUI();
        loadDirectory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);

        this.menu = menu;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            if (modeEnum.equals(Mode.VIEW)) {
                finish();
            }
            if (modeEnum.equals(Mode.CREATE) || modeEnum.equals(Mode.EDIT))
                finish();
        });

        updateMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_menu_attach) {

        } else if (id == R.id.item_menu_send) {
            sendMessageToContact();
        } else if (id == R.id.item_menu_search) {

        } else if (id == R.id.item_menu_edit) {
            Intent intent = ContactActivity.makeIntent(this, Mode.EDIT, contact);
            startActivity(intent);
        } else if (id == R.id.item_menu_save) {
            RequestViewUtils.showRequest(this);
            saveContact(collectDataFromFields());
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

    @OnClick(R.id.tv_additional_fields)
    public void onTvAdditionalFieldsClicked() {
        if (llAdditionalFields.getVisibility() == View.VISIBLE) {
            llAdditionalFields.setVisibility(View.GONE);
            tvAdditionalFields.setText(R.string.contacts_show_additional_fields);
        } else {
            llAdditionalFields.setVisibility(View.VISIBLE);
            tvAdditionalFields.setText(R.string.contacts_hide_additional_fields);
        }
    }

    @OnClick(R.id.tv_home)
    public void onTvHomeClicked() {
        if (llHome.getVisibility() == View.VISIBLE) {
            llHome.setVisibility(View.GONE);
        } else {
            llHome.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_business)
    public void onTvBusinessClicked() {
        if (llBusiness.getVisibility() == View.VISIBLE) {
            llBusiness.setVisibility(View.GONE);
        } else {
            llBusiness.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_other)
    public void onTvOtherClicked() {
        if (llOther.getVisibility() == View.VISIBLE) {
            llOther.setVisibility(View.GONE);
        } else {
            llOther.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onContactsLoad(ContactSettings contactSettings) {
        this.contactSettings = contactSettings;
        RequestViewUtils.hideRequest();
        bindContact();
    }

    @Override
    public void onFail(ErrorType errorType, int serverCode) {
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this.getApplicationContext(), errorType, serverCode);
        finish();
    }

    public enum Mode {
        CREATE, VIEW, EDIT
    }

    public void onBirthdayFieldClick(View view) {
        if (modeEnum.equals(Mode.CREATE) || modeEnum.equals(Mode.EDIT))
            showDatePickerDialog(view);
    }

    public void showDatePickerDialog(View view) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etOtherBirthday.setText(birthdayToString(year, month + 1, dayOfMonth)); //DatePickerDialog starts months from 0
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }


    private void updateMenu() {
        if (menu == null)
            return;


        MenuItem attachItem = menu.findItem(R.id.item_menu_attach);
        MenuItem sendItem = menu.findItem(R.id.item_menu_send);
        MenuItem searchItem = menu.findItem(R.id.item_menu_search);
        MenuItem editItem = menu.findItem(R.id.item_menu_edit);
        MenuItem saveItem = menu.findItem(R.id.item_menu_save);

        if (modeEnum.equals(Mode.VIEW)) {
            attachItem.setVisible(true);
            sendItem.setVisible(true);
            searchItem.setVisible(true);
            editItem.setVisible(true);
            saveItem.setVisible(false);

        } else if (modeEnum.equals(Mode.EDIT) || modeEnum.equals(Mode.CREATE)) {
            attachItem.setVisible(false);
            sendItem.setVisible(false);
            searchItem.setVisible(false);
            editItem.setVisible(false);
            saveItem.setVisible(true);
        }
    }

    private void saveContact(Contact collectDataFromFields) {
        if (modeEnum.equals(Mode.CREATE)) {
            CallCreateContact callCreateContact = new CallCreateContact(collectDataFromFields, new CallRequestResult<String>() {
                @Override
                public void onSuccess(String result) {
                    RequestViewUtils.hideRequest();

                    finish();
                }

                @Override
                public void onFail(ErrorType errorType, int serverCode) {
                    RequestViewUtils.hideRequest();
                    RequestViewUtils.showError(ContactActivity.this, errorType, serverCode);
                }
            });

            callCreateContact.start();
        } else if (modeEnum.equals(Mode.EDIT)) {
            CallUpdateContact callUpdateContact = new CallUpdateContact(collectDataFromFields, new CallRequestResult<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    RequestViewUtils.hideRequest();

                    finish();
                }

                @Override
                public void onFail(ErrorType errorType, int serverCode) {
                    RequestViewUtils.hideRequest();
                    RequestViewUtils.showError(ContactActivity.this, errorType, serverCode);
                }
            });
            callUpdateContact.start();

        }

    }

    private Contact collectDataFromFields() {
        Contact contact = new Contact();
        if(modeEnum.equals(Mode.CREATE)){
            contact.setUUID("");
            contact.setGroupUUIDs(new ArrayList<String>());
            contact.setETag("");
        } else if(modeEnum.equals(Mode.EDIT)){
            contact.setUUID(this.contact.getUUID());
        }
        contact.setFullName(etDisplayName.getText().toString());
        contact.setSkype(etSkype.getText().toString());
        contact.setFacebook(etFacebook.getText().toString());
        contact.setFirstName(etAdditionalFirstName.getText().toString());
        contact.setLastName(etAdditionalLastName.getText().toString());
        contact.setNickName(etAdditionalNickname.getText().toString());
        contact.setPersonalEmail(etHomePersonalEMail.getText().toString());
        contact.setPersonalAddress(etHomeStreetAddress.getText().toString());
        contact.setPersonalCity(etHomeCity.getText().toString());
        contact.setPersonalState(etHomeStateProvince.getText().toString());
        contact.setPersonalZip(etHomeZipCode.getText().toString());
        contact.setPersonalCountry(etHomeCountryRegion.getText().toString());
        contact.setPersonalWeb(etHomeWebPage.getText().toString());
        contact.setPersonalFax(etHomeFax.getText().toString());
        contact.setPersonalPhone(etHomePhone.getText().toString());
        contact.setPersonalMobile(etHomeMobile.getText().toString());
        contact.setBusinessEmail(etBusinessEMail.getText().toString());
        contact.setBusinessCompany(etBusinessCompany.getText().toString());
        contact.setBusinessDepartment(etBusinessDepartment.getText().toString());
        contact.setBusinessJobTitle(etBusinessJobTitle.getText().toString());
        contact.setBusinessOffice(etBusinessOffice.getText().toString());
        contact.setBusinessAddress(etBusinessStreetAddress.getText().toString());
        contact.setBusinessCity(etBusinessCity.getText().toString());
        contact.setBusinessState(etBusinessStateProvince.getText().toString());
        contact.setBusinessZip(etBusinessZipCode.getText().toString());
        contact.setBusinessCountry(etBusinessCountryRegion.getText().toString());
        contact.setBusinessWeb(etBusinessWebPage.getText().toString());
        contact.setBusinessFax(etBusinessFax.getText().toString());
        contact.setBusinessPhone(etBusinessPhone.getText().toString());
        if (!etOtherBirthday.getText().toString().isEmpty()) {
            fillContactBirthDate(contact);
        }
        contact.setOtherEmail(etOtherEMail.getText().toString());
        contact.setNotes(etOtherNotes.getText().toString());
        return contact;
    }

    private void initEditMode(Contact contact) {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        fillContactFields(contact);
        etOtherBirthday.setFocusable(false);
        //displayAdditionalFields(false);

    }

    private void initViewMode(Contact contact) {
        fillContactFields(contact);
        blockFieldsInput();
        blockSpinnersSelect();
        hideEmptyFields();
    }

    private void initCreateMode() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        for (EditText et : etList) {
            et.setText("");
        }
        etOtherBirthday.setFocusable(false);
        //displayAdditionalFields(false);
    }

    private void fillContactFields(Contact contact) {
        etDisplayName.setText(contact.getFullName());
        etSkype.setText(contact.getSkype());
        etFacebook.setText(contact.getFacebook());
        etAdditionalFirstName.setText(contact.getFirstName());
        etAdditionalLastName.setText(contact.getLastName());
        etAdditionalNickname.setText(contact.getNickName());
        etHomePersonalEMail.setText(contact.getPersonalEmail());
        etHomeStreetAddress.setText(contact.getPersonalAddress());
        etHomeCity.setText(contact.getPersonalCity());
        etHomeStateProvince.setText(contact.getPersonalState());
        etHomeZipCode.setText(contact.getPersonalZip());
        etHomeCountryRegion.setText(contact.getPersonalCountry());
        etHomeWebPage.setText(contact.getPersonalWeb());
        etHomeFax.setText(contact.getPersonalFax());
        etHomePhone.setText(contact.getPersonalPhone());
        etHomeMobile.setText(contact.getPersonalMobile());
        etBusinessEMail.setText(contact.getBusinessEmail());
        etBusinessCompany.setText(contact.getBusinessCompany());
        etBusinessDepartment.setText(contact.getBusinessDepartment());
        etBusinessJobTitle.setText(contact.getBusinessJobTitle());
        etBusinessOffice.setText(contact.getBusinessOffice());
        etBusinessStreetAddress.setText(contact.getBusinessAddress());
        etBusinessCity.setText(contact.getBusinessCity());
        etBusinessStateProvince.setText(contact.getBusinessState());
        etBusinessZipCode.setText(contact.getBusinessZip());
        etBusinessCountryRegion.setText(contact.getBusinessCountry());
        etBusinessWebPage.setText(contact.getBusinessWeb());
        etBusinessFax.setText(contact.getBusinessFax());
        etBusinessPhone.setText(contact.getBusinessPhone());
        etOtherBirthday.setText(birthdayToString(contact.getBirthYear(), contact.getBirthMonth(), contact.getBirthDay()));
        etOtherEMail.setText(contact.getOtherEmail());
        etOtherNotes.setText(contact.getNotes());
    }

    private void initUI() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        if (modeEnum.equals(Mode.VIEW)) {
            getSupportActionBar().setTitle("");
        }
    }

    private void loadDirectory() {
        RequestViewUtils.showRequest(this);
        ContactSettingsRepository.getInstance().getContactSettings(this);
    }

    private void bindContact() {
        fillSpinnerEmail();
        fillSpinnerPhone();
        fillSpinnerAddress();
    }

    private void fillSpinnerEmail() {
        NamedEnumsAdapter adapter = new NamedEnumsAdapter(this, android.R.layout.simple_spinner_item, contactSettings.getPrimaryEmail());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmail.setAdapter(adapter);

        spEmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fillSpinnerPhone() {
        NamedEnumsAdapter adapter = new NamedEnumsAdapter(this, android.R.layout.simple_spinner_item, contactSettings.getPrimaryPhone());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPhone.setAdapter(adapter);

        spPhone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fillSpinnerAddress() {
        NamedEnumsAdapter adapter = new NamedEnumsAdapter(this, android.R.layout.simple_spinner_item, contactSettings.getPrimaryAddress());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAddress.setAdapter(adapter);

        spAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void blockFieldsInput() {
        for (EditText et : etList) {
            et.setFocusable(false);
            et.setEnabled(false);
            et.setCursorVisible(false);
            et.setKeyListener(null);
            et.setTextColor(Color.BLACK);
        }
    }

    private void blockSpinnersSelect() {
        for (Spinner sp : spList) {
            sp.setEnabled(false);
            sp.setClickable(false);
        }
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
                RequestViewUtils.showError(ContactActivity.this, errorCode, serverCode);
            }
        });
        callLogout.start();
    }

    private void openLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void displayAdditionalFields(boolean display) {
        if (!display) {
            tvAdditionalFields.setVisibility(View.GONE);
            llAdditionalFields.setVisibility(View.GONE);
        }
    }

    private void sendMessageToContact() {
        if (contact.getViewEmail() != null) {
            Message message = new Message();
            Email email = new Email();
            email.setEmail(contact.getViewEmail());
            EmailCollection emailCollection = new EmailCollection();
            ArrayList<Email> emails = new ArrayList<Email>();
            emails.add(email);
            emailCollection.setEmails(emails);
            message.setTo(emailCollection);
            Intent intent = ComposeActivity.makeIntent(this, message);
            startActivity(intent);
        }
    }

    private void hideEmptyFields() {
        tvAdditionalFields.setVisibility(View.GONE);
        tvHome.setVisibility(View.GONE);
        tvBusiness.setVisibility(View.GONE);
        tvOther.setVisibility(View.GONE);
        int position = 0;
        for (EditText et : etList) {
            if (et.getText().toString().isEmpty()) {
                et.setVisibility(View.GONE);
                tilList.get(position).setVisibility(View.GONE);
            }
            position++;
        }
    }

    private String birthdayToString(int year, int month, int dayOfMonth) {
        String birthday = "";
        if (year != 0 && dayOfMonth != 0) {
            birthday = String.format("%02d.%02d.%02d", dayOfMonth, month, year);
        }
        return birthday;
    }

    private void fillContactBirthDate(Contact contact) {
        String dateString = etOtherBirthday.getText().toString();
        if (!dateString.isEmpty()) {
            String[] dates = dateString.split("\\.");
            int[] birthDate = new int[dates.length];

            for (int i = 0; i < dates.length; i++) {
                birthDate[i] = Integer.parseInt(dates[i]);
            }
            contact.setBirthYear(birthDate[2]);
            contact.setBirthMonth(birthDate[1]);
            contact.setBirthDay(birthDate[0]);
        }
    }
}
