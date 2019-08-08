package com.PrivateRouter.PrivateMail.view.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.creator.MessageCreator;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.EmailCollection;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.logics.LoadContactPoolLogic;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.ComposeActivity;
import com.PrivateRouter.PrivateMail.view.common.CoolLayoutManager;
import com.PrivateRouter.PrivateMail.view.settings.SettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;
import com.PrivateRouter.PrivateMail.view.utils.SoftKeyboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity.LOGOUT;

public class ContactsActivity extends AppCompatActivity
        implements CallRequestResult<Boolean>,
        SwipeRefreshLayout.OnRefreshListener, ContactsAdapter.OnContactClick {

    private boolean fabOpen;
    private static final String TAG = "ContactsActivity";
    public static final int CONTACT = 10;
    String currentFolder = "Contacts";

    @BindView(R.id.rv_contacts)
    RecyclerView rvContacts;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_create_contact)
    View fabCreateContact;

    @BindView(R.id.fab_create_contacts_group)
    View fabCreateContactsGroup;

    @BindView(R.id.fab)
    View fab;

    @BindView(R.id.sl_main)
    SwipeRefreshLayout slMain;

    SearchView searchView;

    public static final String CHOOSE_PARAM = "ChoseMode";

    String currentStorage = "personal";
    ContactsAdapter contactsAdapter;

    LoadContactPoolLogic loadContactLogic;

    LiveData<PagedList<Contact>> pagedListLiveData;


    private boolean firstUpdate = true;
    private boolean requestOnResume;


    private ContactsListModeMediator contactsListModeMediator;
    private Menu menu;
    boolean chooseMode;

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, boolean chooseMode) {
        Intent intent = new Intent(activity, ContactsActivity.class);
        intent.putExtra(ContactsActivity.CHOOSE_PARAM, chooseMode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            chooseMode = getIntent().getBooleanExtra(CHOOSE_PARAM, false);
        }

        initModeMediator();
        initUI();
        initList();

        contactsListModeMediator.setSelectionMode(chooseMode);

        SoftKeyboard.hideKeyboard(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CONTACT && resultCode == RESULT_OK) {
            requestContacts();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initModeMediator() {
        contactsListModeMediator = new ContactsListModeMediator(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        PrivateMailApplication.getInstance().getSyncLogic().pause();

        if (loadContactLogic != null)
            loadContactLogic.cancel(true);
    }

    protected void onPause() {
        super.onPause();
        if (loadContactLogic != null) {
            loadContactLogic.cancel(true);
            loadContactLogic = null;
            slMain.setRefreshing(false);
            requestOnResume = true;
        }
    }

    protected void onResume() {
        super.onResume();
        if (requestOnResume) {
            requestOnResume = false;
            requestContacts();
        }
        if (contactsAdapter != null)
            contactsAdapter.notifyDataSetChanged();
    }

    private void initList() {
        initList("");
    }

    private void initList(String filter) {

        Log.d(TAG, "initList currentFolder=" + currentStorage + " filter = " + filter);

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();

        DataSource.Factory<Integer, Contact> factory;
        if (TextUtils.isEmpty(filter))
            factory = database.messageDao().getAllContactsFactory(currentStorage);
        else
            factory = database.messageDao().getAllFilteredContactsFactory(currentStorage, "%" + filter + "%");

        int contactPerMessage = 10;
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(contactPerMessage)
                .setInitialLoadSizeHint(contactPerMessage * 3)
                .setPrefetchDistance(contactPerMessage)
                .build();

        if (pagedListLiveData != null)
            pagedListLiveData.removeObservers(this);

        pagedListLiveData = new LivePagedListBuilder<>(factory, config)
                .build();


        contactsAdapter = new ContactsAdapter(new ContactDiffUtilCallback(), contactsListModeMediator);

        pagedListLiveData.observe(this, contactPagedList -> {

            contactsAdapter.submitList(contactPagedList);
            if (firstUpdate) {
                requestContacts();
                firstUpdate = false;
            }
        });


        contactsAdapter.setOnContactClick(this);
        rvContacts.setAdapter(contactsAdapter);
    }

    private void initUI() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        slMain.setOnRefreshListener(this);

        rvContacts.setHasFixedSize(true);
        rvContacts.setLayoutManager(new CoolLayoutManager(this));

        fabOpen = false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_menu, menu);

        this.menu = menu;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            if (chooseMode) {
                finish();


            }
            if (contactsListModeMediator.isSelectionMode())
                contactsListModeMediator.closeSelectionMode();
            //TO do for groups
            Intent intent = new Intent(this, GroupsListActivity.class);
            startActivity(intent);
        });

        MenuItem searchItem = menu.findItem(R.id.se_actionBar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        initSearch(searchView);
        updateMenu();
        return super.onCreateOptionsMenu(menu);
    }

    private void updateMenu() {
        if (menu == null)
            return;


        MenuItem searchItem = menu.findItem(R.id.se_actionBar_search);
        MenuItem sendItem = menu.findItem(R.id.item_menu_send);
        MenuItem toRecycleItem = menu.findItem(R.id.item_menu_recyclebin);
        MenuItem chooseItem = menu.findItem(R.id.item_menu_choose);


        sendItem.setVisible(false);
        toRecycleItem.setVisible(false);

        if (chooseMode) {
            chooseItem.setVisible(true);
        } else {
            chooseItem.setVisible(false);

            if (contactsListModeMediator.isSelectionMode()) {
                searchItem.setVisible(false);

                sendItem.setVisible(true);
                toRecycleItem.setVisible(true);

            } else {
                searchItem.setVisible(true);
            }
        }
    }

    private void initSearch(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchText) {

                if (searchText.length() > 2) {
                    searchView.clearFocus();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                initList(query);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_menu_recyclebin) {
            //moveContactToRecyclerBin();
        } else if (id == R.id.item_menu_send) {
            if (!createMessage()) {
                showEmptyEmailAlert();
            }
        } else if (id == R.id.item_menu_choose) {
            if (!chooseContacts()) {
                showEmptyEmailAlert();
            }
        } else if (id == R.id.action_mail) {
            finish();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            setResult(LOGOUT);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEmptyEmailAlert() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.contacts_empty_email))
                .setPositiveButton(R.string.all_ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private boolean chooseContacts() {
        ArrayList<Email> emails = new ArrayList<>();

        List<Contact> contacts = contactsListModeMediator.getSelectionList();
        for (Contact contact : contacts) {
            if (!TextUtils.isEmpty(contact.getViewEmail())) {
                Email email = new Email();
                email.setEmail(contact.getViewEmail());
                email.setDisplayName(contact.getFullName());
                emails.add(email);
            } else {
                return false;
            }
        }

        Intent intent = new Intent();
        intent.putExtra(ComposeActivity.SELECT_EMAIL_PARAM, emails);

        setResult(RESULT_OK, intent);

        finish();
        return true;
    }

    private boolean createMessage() {
        List<Contact> selectedContacts = contactsListModeMediator.getSelectionList();
        if (selectedContacts.isEmpty())
            return true;

        Message message = new Message();
        new MessageCreator(message).fillSender(message);

        EmailCollection emailCollection = new EmailCollection();
        ArrayList<Email> emails = new ArrayList<>();
        for (Contact contact : selectedContacts) {
            if (!TextUtils.isEmpty(contact.getViewEmail())) {
                Email email = new Email();
                email.setEmail(contact.getViewEmail());
                email.setDisplayName(contact.getFullName());
                emails.add(email);
            } else {
                return false;
            }

        }
        emailCollection.setEmails(emails);
        message.setTo(emailCollection);


        Intent intent = ComposeActivity.makeIntent(this, message);
        startActivity(intent);
        finish();

        return true;
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    public void btFabClick() {
        if (!fabOpen) {
            showFABMenu();
        } else {
            closeFABMenu();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab_create_contact)
    public void btNewContactClick() {
        closeFABMenu();
        Intent intent = ContactActivity.makeIntent(ContactsActivity.this, ContactActivity.Mode.CREATE, null);
        startActivityForResult(intent, CONTACT);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab_create_contacts_group)
    public void btNewContactsGroupClick() {
        closeFABMenu();
        Intent intent = new Intent(ContactsActivity.this, GroupActivity.class);
        startActivity(intent);
    }


    private void showFABMenu() {
        fabOpen = true;
        fabCreateContactsGroup.animate().translationY(-getResources().getDimension(R.dimen.fab_transition_y));
        fabCreateContact.animate().translationY(-getResources().getDimension(R.dimen.fab_transition_y) * 2);
    }

    private void closeFABMenu() {
        fabOpen = false;
        fabCreateContactsGroup.animate().translationY(0);
        fabCreateContact.animate().translationY(0);
    }


    @Override
    public void onContactClick(Contact contact, int position) {
        if (contact != null) {
            Intent intent = ContactActivity.makeIntent(ContactsActivity.this, ContactActivity.Mode.VIEW, contact);
            startActivityForResult(intent, CONTACT);
        }
    }

    @Override
    public void onSuccess(Boolean hasNew) {
        loadContactLogic = null;
        slMain.setRefreshing(false);
        if (hasNew) {
            moveScrollToTop();
        }

        SettingsRepository.getInstance().setLastSyncDate(this, new Date().getTime());

        RequestViewUtils.hideRequest();

    }

    private void moveScrollToTop() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> rvContacts.scrollToPosition(0), 1000);

    }

    @Override
    public void onFail(ErrorType errorType, int serverCode) {
        loadContactLogic = null;
        slMain.setRefreshing(false);
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this, errorType, serverCode);
    }

    @Override
    public void onRefresh() {
        requestContacts();
    }

    private void requestContacts() {

        if (loadContactLogic != null) {
            return;
        }

        slMain.setRefreshing(true);

        loadContactLogic = new LoadContactPoolLogic(currentStorage, this);
        loadContactLogic.execute();

    }


    public void onSelectChange(List<Contact> selectedList) {
        if (contactsListModeMediator.isSelectionMode()) {
            updateSelectedTitle(selectedList.size());
        }
    }

    void openSelectMode() {
        fab.setVisibility(View.GONE);
        updateMenu();
        if (!chooseMode) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        }

        updateSelectedTitle(0);
    }

    private void updateSelectedTitle(int count) {
        String title = String.format(getString(R.string.mail_list_selected), count);
        setTitle(title);
    }

    void openNormalMode() {
        fab.setVisibility(View.VISIBLE);
        updateMenu();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_drawer);

        setTitle(currentFolder);
    }
}
