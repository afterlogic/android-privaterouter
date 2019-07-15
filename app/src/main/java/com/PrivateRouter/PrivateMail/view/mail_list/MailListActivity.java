package com.PrivateRouter.PrivateMail.view.mail_list;

import android.app.Activity;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Folder;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.network.logics.LoadMessageLogic;
import com.PrivateRouter.PrivateMail.network.logics.LoadMessagePoolLogic;
import com.PrivateRouter.PrivateMail.network.logics.MoveMessageLogic;
import com.PrivateRouter.PrivateMail.network.requests.CallLogout;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.repository.MessagesRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.ComposeActivity;
import com.PrivateRouter.PrivateMail.view.LoginActivity;
import com.PrivateRouter.PrivateMail.view.common.CoolLayoutManager;
import com.PrivateRouter.PrivateMail.view.contacts.ContactsActivity;
import com.PrivateRouter.PrivateMail.view.folders_list.FoldersListActivity;
import com.PrivateRouter.PrivateMail.view.mail_view.MailViewList;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;
import com.PrivateRouter.PrivateMail.view.mail_view.MailViewActivity;
import com.PrivateRouter.PrivateMail.view.settings.SettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.SoftKeyboard;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MailListActivity extends AppCompatActivity
        implements  CallRequestResult<Boolean>,
                    SwipeRefreshLayout.OnRefreshListener, MailListAdapter.OnMessageClick {

    public static final int LOGOUT = 20;
    public static final int OPEN_CONTACT = 1001;
    public static final int SELECT_FOLDER = 1000;
    public static final String FOLDER_PARAM = "Folder";
    @BindView(R.id.rv_mail_list)
    RecyclerView rvMailList;


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.fab)
    View fab;


    @BindView(R.id.slMain)
    SwipeRefreshLayout slMain;

    SearchView searchView;


    String currentFolder = "Inbox";
    MailListAdapter mailListAdapter;

    LoadMessagePoolLogic loadMessageLogic;

    LiveData<PagedList<Message>> pagedListLiveData;


    private boolean firstUpdate = true;
    private boolean requestOnResume;

    private MailListModeMediator mailListModeMediator;
    private Menu menu;
    private boolean paused = false;

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, String defaultFolder) {
        Intent intent = new Intent(activity, MailListActivity.class);
        intent.putExtra(MailListActivity.FOLDER_PARAM, defaultFolder);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        if (getIntent()!=null) {
            currentFolder = getIntent().getStringExtra(MailListActivity.FOLDER_PARAM);
        }

        initModeSubject();
        openNormalMode();
        initUI();
        initList();

        initUpdateTimer();

        SoftKeyboard.hideKeyboard(this);

    }

    private void initModeSubject() {
        mailListModeMediator = new MailListModeMediator(this);
    }

    void openSelectMode() {
        fab.setVisibility(View.GONE);
        updateMenu();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);


        updateSelectedTitle(0);
    }

    private void updateSelectedTitle(int count) {
        String title = String.format(getString(R.string.mail_list_selected), count);
        setTitle( title );
    }

    void openNormalMode() {
        fab.setVisibility(View.VISIBLE);
        updateMenu();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_drawer);

        setTitle( currentFolder );
    }


    @Override
    protected  void onStop() {
        super.onStop();
        PrivateMailApplication.getInstance().getSyncLogic().pause();

        if (loadMessageLogic!=null)
            loadMessageLogic.cancel(true);
    }

    protected void onPause() {
        super.onPause();
        paused = true;
        if (loadMessageLogic!=null) {
            loadMessageLogic.cancel(true);
            loadMessageLogic = null;
            slMain.setRefreshing(false);
            requestOnResume = true;
        }
    }

    protected void onResume() {
        super.onResume();
        paused = false;
        if (requestOnResume) {
            requestOnResume = false;
            requestMessages();
        }
        if (mailListAdapter!=null) {
            mailListAdapter.notifyDataSetChanged();
        }
    }

    private void initList() {
        initList("");
    }

    private void initList(String filter) {

        Log.d(LoadMessageLogic.TAG, "initList currentFolder=" + currentFolder + " filter = "+filter );

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();

        DataSource.Factory<Integer, Message> factory;
        if (TextUtils.isEmpty(filter))
            factory = database.messageDao().getAllFactory(currentFolder);
        else
            factory = database.messageDao().getAllFilterFactory(currentFolder, "%"+filter+"%");


        int messagePerMessage = 10;
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(messagePerMessage)
                .setInitialLoadSizeHint(messagePerMessage*3)
                .setPrefetchDistance(messagePerMessage)
                .build();

        if (pagedListLiveData!=null)
            pagedListLiveData.removeObservers(this);

        pagedListLiveData = new LivePagedListBuilder<>(factory, config)
                .build();


        mailListAdapter = new MailListAdapter( new MessageDiffUtilCallback(), mailListModeMediator);


        pagedListLiveData.observe(this, (PagedList<Message> messagePagedList) -> {

            if (!paused) {

                mailListAdapter.submitList(messagePagedList);
                checkShowMoreBar();
                if (firstUpdate) {
                    requestMessages();
                    firstUpdate = false;
                }
            }
        });


        mailListAdapter.setOnMessageClick(this);
        rvMailList.setAdapter(mailListAdapter);

        rvMailList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) ) {
                    checkShowMoreBar();

                }
            }
        });
    }

    private void hideShowMoreBar() {

    }
    
    private void checkShowMoreBar() {

        int loadedCount = mailListAdapter.getItemMessageCount();
        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        Folder folder = account.getFolders().getFolder(currentFolder);
        int totalCount = folder.getMeta().getCount();

        if (loadedCount < totalCount) {
            mailListAdapter.setShowMoreBar(true);
            mailListAdapter.notifyDataSetChanged();
        }
        else if (loadedCount == totalCount) {
            mailListAdapter.setShowMoreBar(false);
            mailListAdapter.notifyDataSetChanged();
        }
    }

    private void initUI() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        slMain.setOnRefreshListener(this);

       // rvMailList.setHasFixedSize(true);
        rvMailList.setLayoutManager( new CoolLayoutManager(this));

        rvMailList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mailListAdapter!=null) {
                    mailListAdapter.onLongTap();
                }

                return true;
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        this.menu = menu;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(v -> {

            if (mailListModeMediator.isSelectionMode()) {
                mailListModeMediator.closeSelectionMode();
            }
            else {
                Intent intent = FoldersListActivity.makeIntent(MailListActivity.this, currentFolder );
                startActivityForResult(intent, SELECT_FOLDER);
            }
        });

        MenuItem searchItem = menu.findItem(R.id.se_actionBar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        initSearch(searchView);

        updateMenu();

        return super.onCreateOptionsMenu(menu);
    }

    private void updateMenu() {
        if (menu==null)
            return;


        MenuItem searchItem =  menu.findItem(R.id.se_actionBar_search);
        MenuItem toSpamItem =  menu.findItem(R.id.item_menu_spam);
        MenuItem backFromSpamItem =  menu.findItem(R.id.item_menu_back_from_spam);
        MenuItem toRecycleItem =  menu.findItem(R.id.item_menu_recyclebin);

        toSpamItem.setVisible(false);
        backFromSpamItem.setVisible(false);
        toRecycleItem.setVisible(false);


        if (mailListModeMediator.isSelectionMode() ) {
            searchItem.setVisible(false);

            Account account = LoggedUserRepository.getInstance().getActiveAccount();
            Folder folder = account.getFolders().getFolder(currentFolder);
            FolderType folderType = FolderType.Inbox;
            if (folder != null)
                folderType = FolderType.getByInt(folder.getType());


            if  (!(folderType == FolderType.Sent || folderType == FolderType.Drafts || folderType == FolderType.Spam))
                toSpamItem.setVisible(true);

            if (folderType == FolderType.Spam)
                backFromSpamItem.setVisible(true);

            if (folderType != FolderType.Trash)
                toRecycleItem.setVisible(true);

        }
        else {
            searchItem.setVisible(true);
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

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_logout) {
            logout();
        }
        else if(id == R.id.item_menu_spam){
            moveMessageToSpam();
        } else if(id == R.id.item_menu_back_from_spam){
            moveMessageToInbox();
        } else if(id == R.id.item_menu_recyclebin){
            deleteMessage();
        } else if (id == R.id.action_contacts) {
            Intent intent = ContactsActivity.makeIntent(this, false);
            startActivityForResult(intent, OPEN_CONTACT);
        }


        return super.onOptionsItemSelected(item);
    }

    private void moveMessageToInbox() {
        Integer[] messagesUids = mailListModeMediator.getSelectionList();
        String spamFolder = FolderType.Inbox.name();

        MoveMessageLogic moveMessageLogic = new MoveMessageLogic( currentFolder, spamFolder,
                this::onSuccessMove, this::onFailMove, messagesUids );
        moveMessageLogic.execute();


        RequestViewUtils.showRequest(this);
    }

    private void moveMessageToSpam() {
        Integer[] messagesUids = mailListModeMediator.getSelectionList();
        String spamFolder = FolderType.Spam.name();

        MoveMessageLogic moveMessageLogic = new MoveMessageLogic ( currentFolder, spamFolder,
                this::onSuccessMove, this::onFailMove, messagesUids );
        moveMessageLogic.execute();


        RequestViewUtils.showRequest(this);
    }

    private void deleteMessage() {
        Integer[] messagesUids = mailListModeMediator.getSelectionList();
        String trashFolder = FolderType.Trash.name();

        MoveMessageLogic moveMessageLogic = new MoveMessageLogic( currentFolder, trashFolder,
                this::onSuccessMove, this::onFailMove, messagesUids );
        moveMessageLogic.execute();


        RequestViewUtils.showRequest(this);
    }


    private void onSuccessMove() {
        RequestViewUtils.hideRequest();
        updateSelectedTitle( 0 );
    }


    private void onFailMove(ErrorType errorType, int serverCode) {
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this, errorType, serverCode);
    }

    private void logout() {
        if (loadMessageLogic!=null)
            loadMessageLogic.cancel(true);

        LoggedUserRepository.getInstance().logout(this);

        MessagesRepository.getInstance().clear(() -> {

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
                    RequestViewUtils.showError(MailListActivity.this, errorCode, serverCode);
                }
            });
            callLogout.start();

        });



    }

    private void openLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }



    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    public void btNewEmailClick() {

        Intent intent = new Intent(MailListActivity.this, ComposeActivity.class);
        startActivity(intent);
    }




    @Override
    public void onSuccess(Boolean hasNew) {
        loadMessageLogic = null;
        slMain.setRefreshing(false);
        if (hasNew) {
            moveScrollToTop();
        }

        SettingsRepository.getInstance().setLastSyncDate(this, new Date().getTime() );

        RequestViewUtils.hideRequest();

        PrivateMailApplication.getInstance().getSyncLogic().updateTimer();
    }

    private void moveScrollToTop() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> rvMailList.scrollToPosition(0), 1000);

    }

    @Override
    public void onFail(ErrorType errorType, int serverCode) {
        loadMessageLogic = null;
        slMain.setRefreshing(false);
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this, errorType, serverCode);
        PrivateMailApplication.getInstance().getSyncLogic().updateTimer();
    }

    @Override
    public void onRefresh() {
        requestMessages(true);
    }

    private void requestMessages(   ) {
        requestMessages(false);
    }
    private void requestMessages(  boolean forceCurrent ) {

        if (loadMessageLogic!=null) {
            return;
        }

        slMain.setRefreshing(true);

        hideShowMoreBar();

        loadMessageLogic = new LoadMessagePoolLogic(currentFolder,   this);
        loadMessageLogic.setForceCurrent(forceCurrent);
        loadMessageLogic.execute();

    }


    private void initUpdateTimer() {
        PrivateMailApplication.getInstance().getSyncLogic().setRequestMessagesRunnable(this::requestMessages);
        PrivateMailApplication.getInstance().getSyncLogic().updateTimer();
    }

    @Override
    public void onMessageClick(Message message, int position) {
        Intent intent;
        if (message!=null) {
            if (!message.isThreadMessage()) {
                MailViewList mailViewList = new MailViewList() {
                    @Override
                    public Message getMessage(int index) {
                        return mailListAdapter.getMessage(index);
                    }

                    @Override
                    public int getItemCount() {
                        return mailListAdapter.getItemMessageCount();
                    }
                };
                intent = MailViewActivity.makeIntent(this, mailViewList, position, currentFolder);
            }
            else {
                MailViewList mailViewList = new MailViewList() {
                    @Override
                    public Message getMessage(int index) {
                        return message;
                    }

                    @Override
                    public int getItemCount() {
                        return 1;
                    }
                };
                intent = MailViewActivity.makeIntent(this, mailViewList, position, currentFolder);
            }
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_FOLDER) {
            if (resultCode == RESULT_OK) {
                onChangeFolder( data.getStringExtra( MailListActivity.FOLDER_PARAM ) );
            }
        }
        else if (resultCode == LOGOUT) {
            logout();
        }
    }

    private void onChangeFolder(String newFolder) {

        searchView.setQuery("", false);
        requestOnResume = false;
        firstUpdate = true;
        if (loadMessageLogic!=null)
            loadMessageLogic.cancel(true);

        rvMailList.setAdapter(null);
        currentFolder = newFolder;

        openNormalMode();
        initList();
    }

    public void onSelectChange(List<Message> selectedList) {
        if (mailListModeMediator.isSelectionMode()) {
            updateSelectedTitle( selectedList.size() );
        }
    }

    public void onLoadThread () {
        if (loadMessageLogic!=null)
        loadMessageLogic.cancel(true);
        loadMessageLogic = null;
        //loadMessageLogic.pauseMyTask();
    }

    public void onEndLoadThread() {
        requestMessages();

    }

    public WeakReference<SwipeRefreshLayout> getSwipeRefreshLayout() {
        return new WeakReference<SwipeRefreshLayout>(slMain);
    }
}
