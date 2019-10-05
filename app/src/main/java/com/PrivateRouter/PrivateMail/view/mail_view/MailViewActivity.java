package com.PrivateRouter.PrivateMail.view.mail_view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.creator.ForwardMessageCreator;
import com.PrivateRouter.PrivateMail.dbase.MessageDao;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Folder;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.network.logics.MoveMessageLogic;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CalSetMessagesSeen;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.repository.MessagesRepository;
import com.PrivateRouter.PrivateMail.view.ComposeActivity;
import com.PrivateRouter.PrivateMail.view.contacts.ContactsActivity;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListAdapter;
import com.PrivateRouter.PrivateMail.creator.ReplyMessageCreator;
import com.PrivateRouter.PrivateMail.view.settings.SettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.PrivateRouter.PrivateMail.view.mail_list.MailListActivity.LOGOUT;

public class MailViewActivity extends AppCompatActivity {

    Menu menu;

    @BindView(R.id.vp_main)
    ViewPager pager;

    private Message message;
    private PagerAdapter pagerAdapter;
    private String folderName;
    private boolean blockScrollMessage;

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, boolean blockScrollMessage, int position, String folder) {

        Intent intent = new Intent(activity, MailViewActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("folder", folder);
        intent.putExtra("blockScrollMessage", blockScrollMessage);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_view);
        ButterKnife.bind(this);


        int startPage = 0;
        if (getIntent()!=null && getIntent().getSerializableExtra("position")!=null) {
            startPage = getIntent().getIntExtra("position", 0);
            folderName = getIntent().getStringExtra("folder");
            blockScrollMessage =   getIntent().getBooleanExtra("blockScrollMessage", false);
        }

        setTitle("");
        initPager(startPage);

    }

    private void initPager(int startPage) {
        pagerAdapter = new MessageFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                onSelectPage(position);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        pager.setCurrentItem(startPage);
        onSelectPage(startPage);
        pagerAdapter.notifyDataSetChanged();
    }

    private void onSelectPage(int position) {
        message = MessagesRepository.getInstance().getMessage(position);

        if (message!=null && !message.isSeen() ) {
            sendSeenRequest();
        }
    }

    private void sendSeenRequest() {
        CalSetMessagesSeen calSetMessagesSeen = new CalSetMessagesSeen(new CallRequestResult() {
            @Override
            public void onSuccess(Object result) {
                message.setSeen(true);

                Thread thread = new Thread() {
                    public void run() {
                        MessageDao messageDao = PrivateMailApplication.getInstance().getDatabase().messageDao();
                        messageDao.update(message);
                    }

                };
                thread.start();
            }

            @Override
            public void onFail(ErrorType errorType, int serverCode) {

            }
        });
        calSetMessagesSeen.setMessage(message);
        calSetMessagesSeen.start();

    }

    private void updateMenu() {
        if (menu==null)
            return;

        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        Folder folder =  account.getFolders().getFolder(folderName);
        FolderType folderType = FolderType.Inbox;
        if (folder != null)
            folderType = FolderType.getByInt( folder.getType() );


        if (folderType == FolderType.Sent || folderType == FolderType.Drafts|| folderType == FolderType.Spam)
            menu.findItem(R.id.item_menu_spam).setVisible(false);

        if (folderType != FolderType.Spam)
            menu.findItem(R.id.item_menu_back_from_spam).setVisible(false);

        if (folderType == FolderType.Trash)
            menu.findItem(R.id.item_menu_recyclebin).setVisible(false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mail_view_top_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.menu = menu;
        updateMenu();

        return true;
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        } else if(id == R.id.item_menu_spam){
            moveMessageToSpam();
        } else if(id == R.id.item_menu_back_from_spam){
            moveMessageToInbox();
        } else if(id == R.id.item_menu_recyclebin){
            deleteMessage();
        } else if(id == R.id.item_menu_reply){
            reply();
        } else if(id == R.id.item_menu_reply_all){
            replyAll();
        } else if(id == R.id.item_menu_forward){
            forward();
        } else if (id == R.id.action_mail) {
            finish();
        } else if (id == R.id.action_contacts) {
            Intent intent = ContactsActivity.makeIntent(this, false);
            startActivity(intent);
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            setResult(LOGOUT);
            finish();
        }

        return true;
    }

    private void moveMessageToInbox() {
        String spamFolder = FolderType.Inbox.name();

        MoveMessageLogic moveMessageLogic = new MoveMessageLogic(message.getFolder(), spamFolder,
                this::onSuccessMove, this::onFailMove, message.getUid() );
        moveMessageLogic.execute();


        RequestViewUtils.showRequest(this);
    }

    private void moveMessageToSpam() {
        String spamFolder = FolderType.Spam.name();

        MoveMessageLogic moveMessageLogic = new MoveMessageLogic( message.getFolder(), spamFolder,
                this::onSuccessMove, this::onFailMove, message.getUid() );
        moveMessageLogic.execute();


        RequestViewUtils.showRequest(this);
    }

    private void deleteMessage() {
        String trashFolder = FolderType.Trash.name();

        MoveMessageLogic moveMessageLogic = new MoveMessageLogic(message.getFolder(), trashFolder,
                this::onSuccessMove, this::onFailMove, message.getUid() );
        moveMessageLogic.execute();


        RequestViewUtils.showRequest(this);
    }



    private void onSuccessMove() {
        RequestViewUtils.hideRequest();
        finish();
    }


    private void onFailMove(ErrorType errorType, int serverCode) {
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this, errorType, serverCode);
    }


    private void reply() {
        Message replyMessage = new ReplyMessageCreator(message).createReplyMessage(this, false);
        startComposeActivity(replyMessage);
    }

    private void replyAll() {
        Message replyMessage = new ReplyMessageCreator(message).createReplyMessage(this, true);
        startComposeActivity(replyMessage);
    }

    private void forward() {
        Message forwardMessage = new ForwardMessageCreator(message).createForwardMessage(this );
        startComposeActivity(forwardMessage);
    }

    private void startComposeActivity(Message message) {
        startActivity( ComposeActivity.makeIntent(this, message ) );

    }


    private class MessageFragmentPagerAdapter extends FragmentPagerAdapter {

        public MessageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Message message = MessagesRepository.getInstance().getMessage(position);
            return MailViewFragment.newInstance(message);
        }

        @Override
        public int getCount() {
            if (blockScrollMessage)
                return 1;
            else
                return MessagesRepository.getInstance().getSize();
        }

    }

}
