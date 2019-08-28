package com.PrivateRouter.PrivateMail.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.DecryptCallback;
import com.PrivateRouter.PrivateMail.encryption.EncryptCallback;
import com.PrivateRouter.PrivateMail.model.AttachmentCollection;
import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.EmailCollection;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.network.requests.CallSaveMessage;
import com.PrivateRouter.PrivateMail.network.requests.CallSendMessage;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.requests.CallUploadMessage;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.network.responses.UploadAttachmentResponse;
import com.PrivateRouter.PrivateMail.view.common.ActivityWithRequestPermission;
import com.PrivateRouter.PrivateMail.view.contacts.ContactsActivity;
import com.PrivateRouter.PrivateMail.view.mail_view.AttachmentsAdapter;
import com.PrivateRouter.PrivateMail.view.utils.MessageUtils;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;
import com.PrivateRouter.PrivateMail.view.utils.SoftKeyboard;
import com.PrivateRouter.PrivateMail.view.utils.Utils;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComposeActivity extends ActivityWithRequestPermission implements BottomNavigationView.OnNavigationItemSelectedListener,
        CallRequestResult,  EncryptCallback, DecryptCallback, AttachmentsAdapter.OnRemoveCallback {

    private static final int SELECT_CONTACT = 109;
    public static final String SELECT_EMAIL_PARAM = "emails";
    private static final int CHOOSE_FILE_CODE = 200;

    @BindView(R.id.fwl_recipients)
    FlowLayout fwlRecipients;

    @BindView(R.id.fwl_cc)
    FlowLayout fwlCc;

    @BindView(R.id.fwl_bcc)
    FlowLayout fwlBcc;

    @BindView(R.id.cv_bcc_divider)
    View cvBccDivider;



    @BindView(R.id.ll_recipients)
    LinearLayout llRecipients;

    @BindView(R.id.ll_cc_recipients)
    LinearLayout llCcRecipients;

    @BindView(R.id.ll_bcc_recipients)
    LinearLayout llBccRecipients;

    @BindView(R.id.ib_add_recipients)
    ImageButton ibAddRecipients;

    @BindView(R.id.ib_add_cc_recipients)
    ImageButton ibAddCcRecipients;

    @BindView(R.id.ib_add_bcc_recipients)
    ImageButton ibAddBccRecipients;

    @BindView(R.id.nv_bottom_compose)
    BottomNavigationView nvBottomCompose;

    @BindView(R.id.et_compose_subject)
    EditText etComposeSubject;

    @BindView(R.id.et_compose_message_text)
    EditText etComposeText;

    @BindView(R.id.rv_attachments_list)
    RecyclerView attachmentsList;


    @BindView(R.id.pb_loading_attachments)
    ProgressBar pbLoadingAttachments;

    @BindView(R.id.cv_attachments_divider)
    View cvAttachmentsDivider;

    @BindView(R.id.et_email_to)
    EditText etEmailTo;

    @BindView(R.id.et_email_cc)
    EditText etEmailCc;

    @BindView(R.id.et_email_bcc)
    EditText etEmailBcc;

    private Menu menu;
    private LayoutInflater layoutInflater;

    Message message;
    AttachmentsAdapter attachmentsAdapter;

    String preEncryptedText;
    private Drawable defaultEditTextBackground;

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, Message message) {
        return makeIntent(activity, message, "", "");
    }

    @NonNull
    public static Intent makeIntent(@NonNull Activity activity, Message message, String uploadFileName, String uploadData) {
        Intent intent = new Intent(activity, ComposeActivity.class);
        intent.putExtra("message", message);
        intent.putExtra("uploadFileName", uploadFileName);
        intent.putExtra("uploadData", uploadData);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);


        initMessage();
        prepareMessage();

        updateBottomMenuTitle();


        intiUI();


        bind();

        uploadStartAttachment();

    }

    private void uploadStartAttachment() {
        if (getIntent()!=null && !TextUtils.isEmpty(getIntent().getStringExtra("uploadFileName")) ) {
            String uploadFileName  = getIntent().getStringExtra("uploadFileName");
            String uploadData  = getIntent().getStringExtra("uploadData");

            CallUploadMessage callUploadMessage = new CallUploadMessage(uploadResult);
            callUploadMessage.setFileData(uploadData);
            callUploadMessage.setFileName(uploadFileName);
            callUploadMessage.start();
            pbLoadingAttachments.setVisibility(View.VISIBLE);
        }
    }


    private void intiUI() {

        nvBottomCompose.setOnNavigationItemSelectedListener(this);
        addFieldsFocusReaction();

        etComposeSubject.setMaxLines(Integer.MAX_VALUE); // Or specify a lower value if you want
        etComposeSubject.setHorizontallyScrolling(false);

        etComposeSubject.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ) {
                    etComposeText.requestFocus();
                }
                return true;
            }
        });
        etComposeSubject.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    etComposeText.requestFocus();
                    return true;
                }
                return false;
            }
        });

    }

    private void createEmailFromText(EditText text, EmailCollection emailCollection, Runnable updateListRunnable) {
        String emailStr = text.getText().toString();

        if (EmailValidator.isValidEmail(emailStr)) {

            Email email = new Email();
            email.setEmail(emailStr);

            if (emailCollection.getEmails()==null)
                emailCollection.setEmails(new ArrayList<>());

            emailCollection.getEmails().add(0, email);


            if (updateListRunnable!=null)
                updateListRunnable.run();

        }
        text.setText("");

    }

    private void initMessage() {
        if (getIntent()!=null && getIntent().getSerializableExtra("message")!=null) {
            message = (Message) getIntent().getSerializableExtra("message");
        }
        else {
            message = new Message();
        }
    }


    private void prepareMessage() {
        if (message.getTo()==null) {
            message.setTo(new EmailCollection());
        }
        if (message.getCc()==null) {
            message.setCc(new EmailCollection());
        }
        if (message.getBcc()==null) {
            message.setBcc(new EmailCollection());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_top_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);

        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.item_menu_send) {
            sendMessage();
        } else if (id == R.id.item_menu_save) {
            saveMessage();
        }


        return true;
    }

    private void saveMessage() {
        RequestViewUtils.showRequest(this);
        updateMessage();
        CallSaveMessage callSaveMessage = new CallSaveMessage(new CallRequestResult() {
            @Override
            public void onSuccess(Object result) {
                RequestViewUtils.hideRequest();
            }

            @Override
            public void onFail(ErrorType errorType, int serverCode) {
                RequestViewUtils.hideRequest();
                RequestViewUtils.showError(ComposeActivity.this, errorType, serverCode);
            }
        });
        callSaveMessage.setMessage(this, message);
        callSaveMessage.start();
    }


    private void sendMessage() {
        if (checkMessage() ) {
            RequestViewUtils.showRequest(this);

            updateMessage();
            CallSendMessage callSendMessage = new CallSendMessage(this);
            callSendMessage.setMessage(this, message);
            callSendMessage.start();
        }
    }

    private boolean checkMessage() {
        if ((message.getTo()==null) || (message.getTo().getEmails()==null) || (message.getTo().getEmails().isEmpty()) )  {
            Toast.makeText(this, getString(R.string.compose_select_recipient), Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    private void updateMessage() {
        message.setSubject(etComposeSubject.getText().toString());
        message.setPlain(etComposeText.getText().toString() );

    }

    private void addFieldsFocusReaction(LinearLayout ll, ImageButton ib, EditText et, EmailCollection emailCollection,  Runnable updateRunnable, Runnable onFocusField) {
        ib.setVisibility(View.INVISIBLE);
        et.setVisibility(View.GONE);

        ll.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (onFocusField!=null)
                    onFocusField.run();
                ib.setVisibility(View.VISIBLE);
                et.setVisibility(View.VISIBLE);
                et.requestFocus();
                et.setActivated(true);
                et.setPressed(true);
                et.setSelection(0);
                SoftKeyboard.showKeyboard(this);
            } else if (!et.hasFocus()) {
                ib.setVisibility(View.INVISIBLE);
                et.setVisibility(View.GONE);
            }
        });

        et.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                ib.setVisibility(View.INVISIBLE);
                et.setVisibility(View.GONE);
                createEmailFromText(et, emailCollection, updateRunnable);
            }
            else {
                ib.setVisibility(View.VISIBLE);
            }
        });


        et.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (true) {
                createEmailFromText(et, emailCollection, updateRunnable);
            }
            return true;
        });

    }
    private void addFieldsFocusReaction() {
        addFieldsFocusReaction ( llRecipients,      ibAddRecipients,    etEmailTo,  message.getTo(),    ComposeActivity.this::updateToList, null);
        addFieldsFocusReaction ( llCcRecipients,    ibAddCcRecipients,  etEmailCc,  message.getCc(),    ComposeActivity.this::updateCcToList, this::showCCFields);
        addFieldsFocusReaction ( llBccRecipients,   ibAddBccRecipients, etEmailBcc, message.getBcc(),   ComposeActivity.this::updateBccToList, null);
    }
    private  void clearFieldsFocusReaction() {

        etEmailTo.setVisibility(View.GONE);
        etEmailCc.setVisibility(View.GONE);
        etEmailBcc.setVisibility(View.GONE);
        ibAddRecipients.setVisibility(View.INVISIBLE);
        ibAddCcRecipients.setVisibility(View.INVISIBLE);
        ibAddBccRecipients.setVisibility(View.INVISIBLE);

        llRecipients.setOnFocusChangeListener(null);
        llCcRecipients.setOnFocusChangeListener(null);
        llBccRecipients.setOnFocusChangeListener(null);
    }


    private  void showCCFields() {
        llBccRecipients.setVisibility(View.VISIBLE);
        cvBccDivider.setVisibility(View.VISIBLE);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.ib_add_recipients)
    public void btAddRecipients() {

        openInputDialog(message.getTo(), new Runnable() {
            @Override
            public void run() {
                updateToList();
            }
        });
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.ll_main)
    public void onBgClick() {
        etComposeText.requestFocus();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.ib_add_cc_recipients)
    public void btAddCCRecipients() {

        openInputDialog(message.getCc(), new Runnable() {
            @Override
            public void run() {
                updateCcToList();
            }
        });
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.ib_add_bcc_recipients)
    public void btAddBccRecipients() {

        openInputDialog(message.getBcc(), new Runnable() {
            @Override
            public void run() {
                updateBccToList();
            }
        });
    }

    EmailCollection emailCollectionToAdd;
    Runnable runnableAfterAdd;

    private void openInputDialog(final EmailCollection emailCollection, final Runnable runnable) {
        this.emailCollectionToAdd = emailCollection;
        this.runnableAfterAdd = runnable;
        Intent intent = ContactsActivity.makeIntent(this, true );
        startActivityForResult(intent, SELECT_CONTACT );
    }

    private void bind() {
        updateToList();
        updateCcToList();

        etComposeSubject.setText( message.getSubject() );
        MessageUtils.setMessageBody(message, etComposeText);

        initAttachmentList();
    }

    private void initAttachmentList() {

        if (message.getAttachments()==null)
            message.setAttachments(new AttachmentCollection());

        if (message.getAttachments().getAttachments()==null)
            message.getAttachments().setAttachments(new ArrayList<>());

        List<Attachments> attachments =  message.getAttachments().getAttachments();
        if (attachments.size()>0)
            cvAttachmentsDivider.setVisibility(View.VISIBLE);
        else
            cvAttachmentsDivider.setVisibility(View.GONE);

        attachmentsAdapter = new AttachmentsAdapter(this, attachments, true);
        attachmentsAdapter.setOnRemoveCallback(this);

        attachmentsList.setAdapter(attachmentsAdapter);

    }

    private void addAttachment(Attachments attachments) {
        message.getAttachments().getAttachments().add(attachments);
        attachmentsAdapter.notifyDataSetChanged();
        cvAttachmentsDivider.setVisibility(View.VISIBLE);
    }

    private void updateToList() {
        EmailCollection emailToCollection = message.getTo();
        if (emailToCollection!=null) {
            updateEmailList(emailToCollection.getEmails(), fwlRecipients, this::updateToList);
        }
    }

    private void updateCcToList() {
        EmailCollection emailCcToCollection = message.getCc();
        if (emailCcToCollection!=null) {
            updateEmailList(emailCcToCollection.getEmails(), fwlCc, this::updateCcToList);
        }
    }

    private void updateBccToList() {
        EmailCollection emailCcToCollection = message.getBcc();
        if (emailCcToCollection!=null) {
            updateEmailList(emailCcToCollection.getEmails(), fwlBcc, this::updateBccToList);
        }
    }


    private void updateEmailList(ArrayList<Email> emails, ViewGroup layout, Runnable onRemoveRunnable) {

        if (layout.getChildCount()>1)
            layout.removeViews(0, layout.getChildCount()-1);

        if (emails!=null) {

            for (Email testUserName : emails) {

                View view = createEmailView(testUserName, emails, onRemoveRunnable);

                layout.addView(  view, 0);

            }
        }
    }

    private void removeFromList(ArrayList<Email> emails, Email email) {
        emails.remove(email);

    }

    private View createEmailView(Email testUserName, ArrayList<Email> emails, Runnable onRemoveRunnable) {
        layoutInflater = LayoutInflater.from(this);
        View view = (View) layoutInflater.inflate(R.layout.item_recipient, null, false);
        TextView tvRecipientName = view.findViewById(R.id.tv_recipient_name);
        final ImageButton ibRecipientRemove = view.findViewById(R.id.ib_recipient_remove);

        if (TextUtils.isEmpty(testUserName.getDisplayName() ) )
            tvRecipientName.setText(testUserName.getEmail());
        else
            tvRecipientName.setText(testUserName.getDisplayName());

        ibRecipientRemove.setVisibility(View.GONE);

        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ibRecipientRemove.setVisibility(View.VISIBLE);
                    ibAddRecipients.setVisibility(View.VISIBLE);
                } else {
                    ibRecipientRemove.setVisibility(View.GONE);
                    ibAddRecipients.setVisibility(View.GONE);
                }
            }
        });

        ibRecipientRemove.setOnClickListener(v -> {
            removeFromList(emails, testUserName);
            if (onRemoveRunnable!=null)
                onRemoveRunnable.run();
        });

        ViewGroup.MarginLayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = Utils.getDP(this, 8);
        int marginV = Utils.getDP(this, 4);
        layoutParams.setMargins(0, marginV, margin, marginV);
        view.setLayoutParams(layoutParams);
        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_menu_encrypt) {
            openEncryptPopup();

        } else if (id == R.id.nav_menu_attachments) {
            selectFileFromPhone();
        }
        return false;
    }

    private void selectFileFromPhone() {
        boolean havePermission =  checkAndRequest(Manifest.permission.READ_EXTERNAL_STORAGE, this::selectFileFromPhone);

        if (havePermission) {
            Intent intent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, getString(R.string.settings_select_key_file)), CHOOSE_FILE_CODE);
        }
    }

    private void openEncryptPopup() {
        updateMessage();

        if (preEncryptedText !=null && (MessageUtils.isEncrypted(message) || MessageUtils.isSigned(message) ) ) {
            message.setPlain(preEncryptedText);
            onDecrypt(message);
        }
        else {
            this.preEncryptedText = message.getPlain();

            EncryptDialogFragment encryptDialogFragment = new EncryptDialogFragment();
            encryptDialogFragment.setMessage(message);
            encryptDialogFragment.setEncryptCallback(this);
            encryptDialogFragment.setDecryptCallback(this);
            encryptDialogFragment.show(getSupportFragmentManager(), "encryptDialogFragment");
        }

    }

    @Override
    public void onSuccess(Object result) {
        RequestViewUtils.hideRequest();
        finish();
    }

    @Override
    public void onFail(ErrorType errorType, int serverCode) {
        RequestViewUtils.hideRequest();
        RequestViewUtils.showError(this, errorType, serverCode);
    }



    @Override
    public void onEncrypt(Message message, boolean encrypt, boolean sign) {
        this.message = message;
        bind();

        if (encrypt) {
            setFieldsEnable(false);
            clearFieldsFocusReaction();
        }

        if (sign) {
            etComposeText.setEnabled(false);
        }

        updateBottomMenuTitle();
    }

    private void setFieldsEnable(boolean enable) {
        etComposeText.setEnabled(enable);
        setRecursivelyEnable(llRecipients, enable);
        setRecursivelyEnable(llCcRecipients, enable);
        setRecursivelyEnable(llBccRecipients, enable);
    }



    private void setRecursivelyEnable(View view, boolean status ) {
        view.setEnabled(status);
        if (view instanceof  ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i<viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                if (childView instanceof TextView ) {
                    if (status)
                        ((TextView)childView).setTextColor(getResources().getColor(R.color.color_black));
                    else
                        ((TextView)childView).setTextColor(getResources().getColor(R.color.color_dark_gray));
                }
                setRecursivelyEnable(childView, status);
            }

        }
    }
    @Override
    public void onFail(String description) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(description )
                .setPositiveButton(R.string.all_ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onDecrypt(Message message) {
        this.message = message;
        bind();

        addFieldsFocusReaction();
        setFieldsEnable(true);

        updateBottomMenuTitle();
    }

    private void updateBottomMenuTitle() {

        MenuItem menuItem = nvBottomCompose.getMenu().findItem(R.id.nav_menu_encrypt);
        if (MessageUtils.isEncrypted(message)) {
            menuItem.setIcon(R.drawable.ic_lock_open);
            menuItem.setTitle(R.string.all_decrypt);
        }
        else if (MessageUtils.isSigned(message)) {
            menuItem.setIcon(R.drawable.ic_lock_open);
            menuItem.setTitle(R.string.all_unsign);
        }
        else {
            menuItem.setIcon(R.drawable.ic_lock_outline);
            menuItem.setTitle(R.string.all_encrypt);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_CONTACT) {
                onSelectContact((ArrayList<Email>) data.getSerializableExtra(SELECT_EMAIL_PARAM));
            }
            else if (requestCode == CHOOSE_FILE_CODE) {
                Uri selectedFile = data.getData();
                CallUploadMessage callUploadMessage = new CallUploadMessage(uploadResult);
                callUploadMessage.setFile(selectedFile);
                callUploadMessage.start();
                pbLoadingAttachments.setVisibility(View.VISIBLE);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    CallRequestResult<BaseResponse> uploadResult = new CallRequestResult<BaseResponse>() {
        @Override
        public void onSuccess(BaseResponse result) {
            UploadAttachmentResponse attachmentResponse = (UploadAttachmentResponse) result;
            Attachments attachments = attachmentResponse.getResult().getAttachments();
            addAttachment(attachments);
            pbLoadingAttachments.setVisibility(View.GONE);
        }

        @Override
        public void onFail(ErrorType errorType, int serverCode) {
            pbLoadingAttachments.setVisibility(View.GONE);
            Toast.makeText(ComposeActivity.this, getString(R.string.compose_upload_failed), Toast.LENGTH_SHORT).show();
        }
    };

    private void onSelectContact(ArrayList<Email> stringArrayListExtra) {
        if (emailCollectionToAdd.getEmails()==null) {
            emailCollectionToAdd.setEmails(new ArrayList<Email>());
        }
        emailCollectionToAdd.getEmails().addAll(0, stringArrayListExtra);

        if (runnableAfterAdd!=null)
            runnableAfterAdd.run();
    }


    @Override
    public void onRemove(Attachments attachments) {
        if (attachmentsAdapter.getItemCount() ==0 )
            cvAttachmentsDivider.setVisibility(View.GONE);
    }
}
