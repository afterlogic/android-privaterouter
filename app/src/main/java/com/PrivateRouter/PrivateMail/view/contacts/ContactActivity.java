package com.PrivateRouter.PrivateMail.view.contacts;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.dbase.AsyncDbaseOperation;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.ContactSettings;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.EmailCollection;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.Group;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.NamedEnums;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallCreateContact;
import com.PrivateRouter.PrivateMail.network.requests.CallLogout;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.network.requests.CallUpdateContact;
import com.PrivateRouter.PrivateMail.repository.ContactSettingsRepository;
import com.PrivateRouter.PrivateMail.repository.GroupsRepository;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.ComposeActivity;
import com.PrivateRouter.PrivateMail.view.EmailValidator;
import com.PrivateRouter.PrivateMail.view.LoginActivity;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.settings.SettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.CustomLinearLayoutManager;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;
import com.PrivateRouter.PrivateMail.view.utils.VCardHelper;

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
    private Enum<Mode> modeEnum = Mode.VIEW;
    private Contact contact;
    private GroupsListMediator groupsListMediator;
    private NamedEnumsAdapter emailsAdapter;
    private NamedEnumsAdapter phonesAdapter;
    private NamedEnumsAdapter addressAdapter;


    public static final int OPEN_CONTACT = 111;
    public static final int UPDATED_CONTACT = 112;

    //region Butterknife binds
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_display_name)
    TextInputEditText etDisplayName;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.et_primary_email)
    EditText etPrimaryEmail;
    @BindView(R.id.sp_email)
    Spinner spEmail;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.et_primary_phone)
    EditText etPrimaryPhone;
    @BindView(R.id.sp_phone)
    Spinner spPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.et_primary_address)
    EditText etPrimaryAddress;
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
    @BindView(R.id.tv_groups)
    TextView tvGroups;
    @BindView(R.id.rv_groups)
    RecyclerView rvGroups;

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

    @BindViews({R.id.et_primary_address,
            R.id.et_primary_email,
            R.id.et_primary_phone})
    List<EditText> primaryEtList;

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

    @BindViews({R.id.et_home_personal_e_mail,
            R.id.et_business_e_mail,
            R.id.et_other_e_mail})
    List<EditText> etEmailsList;

    @BindViews({R.id.et_home_street_address,
            R.id.et_business_street_address})
    List<EditText> etAddressesList;

    @BindViews({R.id.et_home_phone,
            R.id.et_business_phone,
            R.id.et_home_mobile})
    List<EditText> etPhonesList;
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

        initGroupsListMediator();

        contact = new Contact();
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
        loadContactSettings();

        initUI();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATED_CONTACT && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            openComposeScreenWithVCFCard();
        } else if (id == R.id.item_menu_send) {
            openComposeScreenWithContact();
        } else if (id == R.id.item_menu_search) {
            searchMailsWithContacts();
        } else if (id == R.id.item_menu_edit) {
            Intent intent = ContactActivity.makeIntent(this, Mode.EDIT, contact);
            startActivityForResult(intent, UPDATED_CONTACT);
        } else if (id == R.id.item_menu_save) {
            if (checkEmailFieldsDataCorrect()) {
                RequestViewUtils.showRequest(this);
                saveContact(collectDataFromFields());
            }
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

    private void openComposeScreenWithVCFCard() {
        Message message = new Message();
        EmailCollection emailCollection = new EmailCollection();
        ArrayList<Email> emails = new ArrayList<Email>();
        emailCollection.setEmails(emails);
        message.setTo(emailCollection);


        String vCardData = VCardHelper.getVCardData(contact);

        String fileName = contact.getViewEmail() + ".vcf";

        Intent intent = ComposeActivity.makeIntent(this, message, fileName, vCardData);
        startActivity(intent);

    }

    private void searchMailsWithContacts() {
        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        String folder = account.getFolders().getFolderName(FolderType.Inbox);

        Intent broadcastIntent = new Intent(MailListActivity.SEARCH_WORD);
        broadcastIntent.putExtra(MailListActivity.SEARCH_WORD, contact.getViewEmail());
        sendBroadcast(broadcastIntent);

        Intent intent = MailListActivity.makeIntent(this, folder, MailListActivity.EMAIL_SEARCH_PREFIX + contact.getViewEmail());
        startActivity(intent);
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.tv_additional_fields)
    public void onTvAdditionalFieldsClicked() {
        if (llAdditionalFields.getVisibility() == View.VISIBLE) {
            llAdditionalFields.setVisibility(View.GONE);
            tvAdditionalFields.setText(R.string.contacts_show_additional_fields);
            primaryFieldsChangeMode(false);

        } else {
            llAdditionalFields.setVisibility(View.VISIBLE);
            tvAdditionalFields.setText(R.string.contacts_hide_additional_fields);
            primaryFieldsChangeMode(true);
        }
        updatePrimaryFields();
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
        bindContact();
        RequestViewUtils.hideRequest();
    }


    @Override
    public void onFail(ErrorType errorType, int serverCode) {
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this.getApplicationContext(), errorType, serverCode);
        finish();
    }

    public void onSelectGroupChange(List<Group> selected) {

    }

    public enum Mode {
        CREATE,
        VIEW,
        EDIT
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

        MenuItem actionMail = menu.findItem(R.id.action_mail);
        MenuItem actionContacts = menu.findItem(R.id.action_contacts);
        MenuItem actionSettings = menu.findItem(R.id.action_settings);
        MenuItem actionLogout = menu.findItem(R.id.action_logout);




        if (modeEnum.equals(Mode.VIEW)) {
            attachItem.setVisible(true);
            sendItem.setVisible(true);
            searchItem.setVisible(true);
            editItem.setVisible(true);
            saveItem.setVisible(false);
            actionMail.setVisible(true);
            actionContacts.setVisible(true);
            actionSettings.setVisible(true);
            actionLogout.setVisible(true);

        } else if (modeEnum.equals(Mode.EDIT) || modeEnum.equals(Mode.CREATE)) {
            attachItem.setVisible(false);
            sendItem.setVisible(false);
            searchItem.setVisible(false);
            editItem.setVisible(false);
            saveItem.setVisible(true);
            actionMail.setVisible(false);
            actionContacts.setVisible(false);
            actionSettings.setVisible(false);
            actionLogout.setVisible(false);
        }
    }

    private void saveContact(Contact contact) {
        if (modeEnum.equals(Mode.CREATE)) {
            CallCreateContact callCreateContact = new CallCreateContact(contact, new CallRequestResult<String>() {

                @Override
                public void onSuccess(String result) {
                    contact.setUUID(result);
                    onContactOnServerUpdated(contact);
                }

                @Override
                public void onFail(ErrorType errorType, int serverCode) {
                    RequestViewUtils.hideRequest();
                    RequestViewUtils.showError(ContactActivity.this, errorType, serverCode);
                }
            });

            callCreateContact.start();

        } else if (modeEnum.equals(Mode.EDIT)) {
            CallUpdateContact callUpdateContact = new CallUpdateContact(contact, new CallRequestResult<String>() {

                @Override
                public void onSuccess(String result) {
                    onContactOnServerUpdated(contact);
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
        if (modeEnum.equals(Mode.CREATE)) {
            contact.setUUID("");
            contact.setGroupUUIDs(new ArrayList<String>());
            contact.setETag("");
        } else if (modeEnum.equals(Mode.EDIT)) {
            contact.setUUID(this.contact.getUUID());
            contact.setETag(this.contact.getETag());
            contact.setGroupUUIDs(this.contact.getGroupUUIDs());
            if (contact.getGroupUUIDs() == null) {
                contact.setGroupUUIDs(new ArrayList<String>());
            }
        }
        contact.setFullName(etDisplayName.getText().toString());
        contact.setPrimaryEmail(this.contact.getPrimaryEmail());
        contact.setPrimaryAddress(this.contact.getPrimaryAddress());
        contact.setPrimaryPhone(this.contact.getPrimaryPhone());
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
        contact.setGroupUUIDs(groupsListMediator.getSelectionUUIDList());
        return contact;
    }

    private void initEditMode(Contact contact) {

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        fillContactFields(contact);
        llAdditionalFields.setVisibility(View.GONE);
        tvAdditionalFields.setText(R.string.contacts_show_additional_fields);
        etOtherBirthday.setFocusable(false);
        setTitle(getString(R.string.contacts_edit));
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
        llAdditionalFields.setVisibility(View.GONE);
        tvAdditionalFields.setText(R.string.contacts_show_additional_fields);
        etOtherBirthday.setFocusable(false);
        setTitle(getString(R.string.contacts_create));
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
        primaryFieldsChangeMode(false);
        initPrimaryFieldsListeners();
    }

    private void loadContactSettings() {
        RequestViewUtils.showRequest(this);
        ContactSettingsRepository.getInstance().getContactSettings(this);
    }


    private void bindContact() {
        fillSpinnerEmail();
        fillSpinnerPhone();
        fillSpinnerAddress();
        fillGroupsList();
        updatePrimaryFields();
    }

    private void fillSpinnerEmail() {
        emailsAdapter = new NamedEnumsAdapter(this, R.layout.item_spinner, contactSettings.getPrimaryEmail(), etEmailsList);
        spEmail.setAdapter(emailsAdapter);

        int positionInList = getIndexInList(contactSettings.getPrimaryEmail(), contact.getPrimaryEmail());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                spEmail.setSelection(positionInList);
            }
        }, 100);

        spEmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedEmailNumberInList = getIndexInList(contactSettings.getPrimaryEmail(), position);
                NamedEnums namedEnum = emailsAdapter.getItem(selectedEmailNumberInList);
                contact.setPrimaryEmail(namedEnum.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void fillSpinnerPhone() {
        phonesAdapter = new NamedEnumsAdapter(this, R.layout.item_spinner, contactSettings.getPrimaryPhone(), etPhonesList);
        spPhone.setAdapter(phonesAdapter);

        int positionInList = getIndexInList(contactSettings.getPrimaryPhone(), contact.getPrimaryPhone());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                spPhone.setSelection(positionInList);
            }
        }, 200);

        spPhone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedPhoneNumber = getIndexInList(contactSettings.getPrimaryPhone(), position);
                NamedEnums namedEnum = phonesAdapter.getItem(selectedPhoneNumber);
                contact.setPrimaryPhone(namedEnum.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fillSpinnerAddress() {
        addressAdapter = new NamedEnumsAdapter(this, R.layout.item_spinner, contactSettings.getPrimaryAddress(), etAddressesList);
        spAddress.setAdapter(addressAdapter);

        int positionInList = getIndexInList(contactSettings.getPrimaryAddress(), contact.getPrimaryAddress());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                spAddress.setSelection(positionInList);
            }
        }, 300);

        spAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedAddressNumber = getIndexInList(contactSettings.getPrimaryAddress(), position);
                NamedEnums namedEnum = addressAdapter.getItem(selectedAddressNumber);
                contact.setPrimaryAddress(namedEnum.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        for (EditText et : primaryEtList) {
            et.setFocusable(false);
            et.setEnabled(false);
            et.setCursorVisible(false);
            et.setKeyListener(null);
            et.setBackgroundColor(Color.TRANSPARENT);
            et.setTextColor(color);
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

    private void openComposeScreenWithContact() {
        if (contact.getViewEmail() != null) {
            Message message = new Message();
            EmailCollection emailCollection = new EmailCollection();
            ArrayList<Email> emails = new ArrayList<Email>();


            if (!TextUtils.isEmpty(contact.getBusinessEmail())) {
                Email email = new Email();
                email.setEmail(contact.getBusinessEmail());
                emails.add(email);
            }


            if (!TextUtils.isEmpty(contact.getOtherEmail())) {
                Email email = new Email();
                email.setEmail(contact.getOtherEmail());
                emails.add(email);
            }


            if (!TextUtils.isEmpty(contact.getPersonalEmail())) {
                Email email = new Email();
                email.setEmail(contact.getPersonalEmail());
                emails.add(email);
            }

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

    private int getIndexInList(ArrayList<NamedEnums> list, int selectedElement) {
        for (int i = 0; i <= list.size(); i++) {
            if (list.get(i).getId() == selectedElement) {
                return i;
            }
        }
        return 0;
    }

    private void onContactOnServerUpdated(Contact contact) {
        AsyncDbaseOperation asyncDbaseOperation = new AsyncDbaseOperation();
        asyncDbaseOperation.setRunnableOperation(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
                database.messageDao().insertContact(contact);
            }
        });

        asyncDbaseOperation.setOnFinishCallback(new Runnable() {
            @Override
            public void run() {
                RequestViewUtils.hideRequest();
                setResult(RESULT_OK);
                finish();
            }
        });

        asyncDbaseOperation.execute();
    }

    private void initGroupsListMediator() {
        groupsListMediator = new GroupsListMediator(this);
    }

    private void fillGroupsList() {
        GroupsRepository.getInstance().load(new OnGroupsLoadCallback() {
            @Override
            public void onGroupsLoad(ArrayList<Group> allGroups) {
                GroupsAdapter.GroupWorkMode mode;
                ArrayList<Group> groups;
                ArrayList<Group> selectGroups = getSelectedGroups(allGroups);
                if (modeEnum == Mode.VIEW) {
                    mode = GroupsAdapter.GroupWorkMode.SINGLE_MODE;
                    groups = selectGroups;
                } else {
                    groups = allGroups;
                    mode = GroupsAdapter.GroupWorkMode.CHECK_MODE;


                    groupsListMediator.setSelectedGroups(selectGroups);
                }


                GroupsAdapter groupsAdapter = new GroupsAdapter(mode, groups, groupsListMediator);
                CustomLinearLayoutManager customLayoutManager = new CustomLinearLayoutManager(ContactActivity.this,
                        LinearLayoutManager.VERTICAL, false);
                rvGroups.setLayoutManager(customLayoutManager);
                rvGroups.setAdapter(groupsAdapter);
            }

            @Override
            public void onGroupsLoadFail(ErrorType errorType, int serverCode) {

            }
        }, false);

    }

    private ArrayList<Group> getSelectedGroups(ArrayList<Group> allGroups) {
        ArrayList<Group> groups = new ArrayList<>();
        if (contact.getGroupUUIDs() != null) {
            for (Group group : allGroups) {
                if (contact.getGroupUUIDs().contains(group.getUUID())) {
                    groups.add(group);
                }
            }
        }
        return groups;
    }


    private void showSpinners(boolean show) {
        for (Spinner sp : spList) {
            sp.setEnabled(show);
            sp.setClickable(show);
            if (show) {
                sp.setVisibility(View.VISIBLE);
            } else {
                sp.setVisibility(View.GONE);
            }
        }
    }

    private void primaryFieldsChangeMode(boolean changing) {
        if (!changing) {
            showSpinners(false);
            etPrimaryPhone.setVisibility(View.VISIBLE);
            etPrimaryEmail.setVisibility(View.VISIBLE);
            etPrimaryAddress.setVisibility(View.VISIBLE);
        } else {
            showSpinners(true);
            etPrimaryPhone.setVisibility(View.GONE);
            etPrimaryEmail.setVisibility(View.GONE);
            etPrimaryAddress.setVisibility(View.GONE);
        }
    }

    private void fillPrimaryEmail(String primaryEmailValue) {
        switch (primaryEmailValue) {
            case "Personal":
                if (contact.getPersonalEmail() != null)
                    etPrimaryEmail.setText(etHomePersonalEMail.getText().toString());
                break;
            case "Business":
                if (contact.getBusinessEmail() != null)
                    etPrimaryEmail.setText(etBusinessEMail.getText().toString());
                break;
            case "Other":
                if (contact.getOtherEmail() != null)
                    etPrimaryEmail.setText(etOtherEMail.getText().toString());
                break;
        }
    }

    private void fillPrimaryPhone(String primaryPhoneValue) {
        switch (primaryPhoneValue) {
            case "Personal":
                if (contact.getPersonalPhone() != null)
                    etPrimaryPhone.setText(etHomePhone.getText().toString());
                break;
            case "Business":
                if (contact.getBusinessPhone() != null)
                    etPrimaryPhone.setText(etBusinessPhone.getText().toString());
                break;
            case "Mobile":
                if (contact.getPersonalMobile() != null)
                    etPrimaryPhone.setText(etHomeMobile.getText().toString());
                break;
        }
    }

    private void fillPrimaryAddress(String primaryAddressValue) {
        switch (primaryAddressValue) {
            case "Personal":
                if (contact.getPersonalAddress() != null)
                    etPrimaryAddress.setText(etHomeStreetAddress.getText().toString());
                break;
            case "Business":
                if (contact.getBusinessAddress() != null)
                    etPrimaryAddress.setText(etBusinessStreetAddress.getText().toString());
                break;
        }
    }

    private void initPrimaryFieldsListeners() {
        etPrimaryEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (contact.getPrimaryEmail()) {
                    case 0:
                        etHomePersonalEMail.setText(s.toString());
                        break;
                    case 1:
                        etBusinessEMail.setText(s.toString());
                        break;
                    case 2:
                        etOtherEMail.setText(s.toString());
                        break;
                }
            }
        });
        etPrimaryAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (contact.getPrimaryAddress()) {
                    case 0:
                        etHomeStreetAddress.setText(s.toString());
                        break;
                    case 1:
                        etBusinessStreetAddress.setText(s.toString());
                        break;
                }
            }
        });
        etPrimaryPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (contact.getPrimaryPhone()) {
                    case 0:
                        etHomeMobile.setText(s.toString());
                        break;
                    case 1:
                        etHomePhone.setText(s.toString());
                        break;
                    case 2:
                        etBusinessPhone.setText(s.toString());
                        break;
                }
            }
        });
        for (EditText editText : etEmailsList) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    emailsAdapter.notifyDataSetChanged();
                }
            });
        }
        for (EditText editText : etAddressesList) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    addressAdapter.notifyDataSetChanged();
                }
            });
        }
        for (EditText editText : etPhonesList) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    phonesAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    private boolean checkEmailFieldsDataCorrect() {
        if (!TextUtils.isEmpty(etHomePersonalEMail.getText().toString()) && !EmailValidator.isValidEmail(etHomePersonalEMail.getText().toString())) {
            etHomePersonalEMail.setError(getString(R.string.all_email_is_incorrect));
            etHomePersonalEMail.requestFocus();
            return false;
        } else if (!TextUtils.isEmpty(etBusinessEMail.getText().toString()) && !EmailValidator.isValidEmail(etBusinessEMail.getText().toString())) {
            etBusinessEMail.setError(getString(R.string.all_email_is_incorrect));
            etBusinessEMail.requestFocus();
            return false;
        } else if (!TextUtils.isEmpty(etOtherEMail.getText().toString()) && !EmailValidator.isValidEmail(etOtherEMail.getText().toString())) {
            etOtherEMail.setError(getString(R.string.all_email_is_incorrect));
            etOtherEMail.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void updatePrimaryFields() {
        int positionAddressInList = getIndexInList(contactSettings.getPrimaryAddress(), contact.getPrimaryAddress());
        String primaryAddressValue = contactSettings.getPrimaryAddress().get(positionAddressInList).getName();
        fillPrimaryAddress(primaryAddressValue);

        int positionEmailInList = getIndexInList(contactSettings.getPrimaryEmail(), contact.getPrimaryEmail());
        String primaryEmailValue = contactSettings.getPrimaryEmail().get(positionEmailInList).getName();
        fillPrimaryEmail(primaryEmailValue);

        int positionPhoneInList = getIndexInList(contactSettings.getPrimaryPhone(), contact.getPrimaryPhone());
        String primaryPhoneValue = contactSettings.getPrimaryPhone().get(positionPhoneInList).getName();
        fillPrimaryPhone(primaryPhoneValue);

        emailsAdapter.notifyDataSetChanged();
        phonesAdapter.notifyDataSetChanged();
        addressAdapter.notifyDataSetChanged();
    }
}
