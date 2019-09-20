package com.PrivateRouter.PrivateMail.view.folders_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Folder;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.network.logics.LoadFolderLogic;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.settings.SettingsActivity;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FoldersListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FolderAdapter.OnFolderClick {
    @BindView(R.id.iv_avatar)
    ImageView avatar;

    @BindView(R.id.tv_username)
    TextView tvUserName;

    @BindView(R.id.tv_usermail)
    TextView tvUserMail;

    @BindView(R.id.rv_folders_list)
    RecyclerView rvFoldersList;

    @BindView(R.id.slMain)
    SwipeRefreshLayout slMain;

    FolderAdapter folderAdapter;
    private String currentFolder;
    private boolean currentUnreadOnly;


    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, String folder, boolean unreadOnly) {
        Intent intent = new Intent(activity, FoldersListActivity.class);
        intent.putExtra("folder", folder);
        intent.putExtra("unreadOnly", unreadOnly);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders_list);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.left_to_right, R.anim.hold);


        if (getIntent()!=null) {
            currentFolder = getIntent().getStringExtra("folder");
            currentUnreadOnly = getIntent().getBooleanExtra("unreadOnly", false);
        }
        slMain.setOnRefreshListener(this);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        bindUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return true;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_avatar)
    public void ivAvatarClick() {

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_settings)
    public void ivSettingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void bindUI() {

        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        tvUserMail.setText(account.getEmail());

        String name = account.getFriendlyName();
        if (TextUtils.isEmpty(name))
            name = LoggedUserRepository.getInstance().getLogin();

        tvUserName.setText( name );

        folderAdapter = new FolderAdapter(this, account.getFoldersWithSubFolder(), currentFolder );
        folderAdapter.setOnFolderClick(this);
        rvFoldersList.setAdapter(folderAdapter);


    }

    @Override
    public void onRefresh() {
        LoadFolderLogic loadFolderLogic = new LoadFolderLogic(folderCollection -> {

            Account account = LoggedUserRepository.getInstance().getActiveAccount();
            account.setFolders( folderCollection );

            LoggedUserRepository.getInstance().save( FoldersListActivity.this );

            folderAdapter.setFolderList( account.getFoldersWithSubFolder() );
            slMain.setRefreshing(false);

            bindUI();

        }, (errorType, errorCode) -> {
            slMain.setRefreshing(false);
            RequestViewUtils.hideRequest();
            RequestViewUtils.showError(FoldersListActivity.this, errorType, errorCode);
        });
        loadFolderLogic.execute();


    }

    @Override
    public void onFolderClick(Folder folder) {
        openFolder(folder, false);
    }

    @Override
    public void onFolderUnreadClick(Folder folder) {
        openFolder(folder, true);
    }

    private void openFolder(Folder folder, boolean unreadOnly) {

        if (unreadOnly && folder.getType() == FolderType.Drafts.getId())
            unreadOnly = false;

        if (!folder.getFullName().equals(currentFolder) || currentUnreadOnly != unreadOnly) {

            Intent intent = new Intent();
            intent.putExtra(MailListActivity.FOLDER_PARAM, folder.getFullName());
            if (folder.getFullName().equals(FolderType.VirtualStarred) ) {
                Account account = LoggedUserRepository.getInstance().getActiveAccount();
                intent.putExtra(MailListActivity.PERFORM_FOLDER_PARAM, account.getFolders().getFolderName(FolderType.Inbox) );
            }

            intent.putExtra(MailListActivity.UNREAD_ONLY_PARAM, unreadOnly );

            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.right_to_left);
    }

}
