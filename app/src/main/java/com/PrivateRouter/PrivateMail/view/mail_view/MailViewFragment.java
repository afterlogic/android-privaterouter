package com.PrivateRouter.PrivateMail.view.mail_view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.DecryptCallback;
import com.PrivateRouter.PrivateMail.encryption.EncryptCallback;
import com.PrivateRouter.PrivateMail.encryption.SignHelper;
import com.PrivateRouter.PrivateMail.encryption.VerifyTask;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.network.requests.CallSetEmailSafety;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.EncryptDialogFragment;
import com.PrivateRouter.PrivateMail.view.common.FragmentWithRequestPermission;
import com.PrivateRouter.PrivateMail.view.utils.DateUtils;
import com.PrivateRouter.PrivateMail.view.utils.EmailUtils;
import com.PrivateRouter.PrivateMail.view.utils.MessageUtils;
import com.PrivateRouter.PrivateMail.view.utils.RequestViewUtils;
import com.PrivateRouter.PrivateMail.view.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.PrivateRouter.PrivateMail.PrivateMailApplication.getContext;

public  class MailViewFragment extends FragmentWithRequestPermission implements  AttachmentsAdapter.OnAttachmentClick,
        EncryptCallback, DecryptCallback {
    static final String ARGUMENT_MESSAGE = "message";

    @BindView(R.id.tv_subject)
    TextView tvSubject;

    @BindView(R.id.tv_short_info_sender)
    TextView tvMessageSender;

    @BindView(R.id.tv_short_info_date)
    TextView tvMessageDate;

    @BindView(R.id.rv_attachments_list)
    RecyclerView attachmentsList;

    @BindView(R.id.cv_attachments_divider)
    View cvAttachmentsDivider;

    @BindView(R.id.wv_message)
    WebView wvMessage;

    @BindView(R.id.tv_sender)
    TextView tvSender;

    @BindView(R.id.tv_recipient)
    TextView tvRecipient;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_hide_details)
    TextView tvHideDetails;

    @BindView(R.id.cl_full_info)
    ViewGroup clFullInfo;

    @BindView(R.id.cl_short_info)
    ViewGroup clShortInfo;

    @BindView(R.id.сl_picture_blocked)
    ViewGroup сlPictureBlocked;

    @BindView(R.id.nv_bottom_mail_view)
    BottomNavigationView nvBottomMailView;

    Message message;


    private boolean showExternalPicture = false;

    static MailViewFragment newInstance(Message message) {
        MailViewFragment pageFragment = new MailViewFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_MESSAGE, message);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            message = (Message) getArguments().getSerializable(ARGUMENT_MESSAGE);

        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_view, null);

        ButterKnife.bind(this, view);

        Utils.updateBottomMenuColors(nvBottomMailView);
        nvBottomMailView.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if(id == R.id.nav_menu_encrypt) {

                if (MessageUtils.isEncrypted(message)) {
                    openEncryptPopup();
                }
                else {
                    verifyMessage();
                }
            }
            return false;
        });

        initUI();
        showShortInfo();
        bind();

        return view;
    }

    private void initUI() {

/*
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        attachmentsList.addItemDecoration(dividerItemDecoration);
*/

        if (SettingsRepository.getInstance().isNightMode(getActivity() ) ) {
            tvHideDetails.setTextColor( getActivity().getResources().getColor(R.color.color_white));
        }
        else {
            tvHideDetails.setTextColor( getActivity().getResources().getColor(R.color.colorPrimary));
        }

    }


    private void bind() {
        tvSubject.setText(message.getSubject());



        String fromLine = EmailUtils.getString(getActivity(), message.getFrom() );
        tvMessageSender.setText( fromLine );
        tvSender.setText(fromLine);

        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        tvRecipient.setText( account.getEmail() );

        String fullDate = DateUtils.getFullDate(getActivity(), message.getTimeStampInUTC());
        tvDate.setText(fullDate);

        String shortDate = DateUtils.getShortDate(getActivity(), message.getTimeStampInUTC());
        tvMessageDate.setText(shortDate);



        String messageText;

        if (TextUtils.isEmpty(message.getHtml() )) {
            messageText = message.getPlain();
        }
        else {
            messageText = message.getHtml();
        }



        ArrayList<Attachments> clearAttachmentList = new ArrayList<>();

        if (message.getAttachments()!=null   ) {

            for (Attachments attach : message.getAttachments().getAttachments() ) {

                String dataXpattern = "data-x-src-cid=\""+attach.getCID()+"\"";
                if ( !TextUtils.isEmpty( message.getHtml() ) && messageText.contains( dataXpattern  ) ) {
                    //String url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvn4PAkWp9PqknNp37wWUgeML5mevA-ykYU16oowD-9EVVobMc";
                    String url = ApiFactory.getUrl() + attach.getActions().getView().getUrl();
                    String newSrc = "src=\""+url+"\"";
                    messageText = messageText.replace( dataXpattern , newSrc);
                }
                else {
                    clearAttachmentList.add(attach);
                }
            }


        }


        if (message.isHasExternals()) {
            if (message.isSafety()) {
                showExternalPicture = true;
                сlPictureBlocked.setVisibility(View.GONE);
            }
            else
                сlPictureBlocked.setVisibility(View.VISIBLE);
        }
        else {
            сlPictureBlocked.setVisibility(View.GONE);
        }


        if (showExternalPicture) {
            messageText = messageText.replace("data-x-src", "src");
        }


        if (  clearAttachmentList.size()>0 ) {
            AttachmentsAdapter mailViewAdapter = new AttachmentsAdapter(getActivity(), clearAttachmentList, false );
            mailViewAdapter.setOnAttachmentClick(this);
            attachmentsList.setAdapter(mailViewAdapter);

            attachmentsList.setVisibility(View.VISIBLE);
            cvAttachmentsDivider.setVisibility(View.VISIBLE);
        }
        else {
            attachmentsList.setVisibility(View.GONE);
            cvAttachmentsDivider.setVisibility(View.GONE);
        }


        wvMessage.setBackgroundColor(Color.TRANSPARENT);
        MessageUtils.setMessageBody(messageText, wvMessage);

        updateBottomMenu();
    }



    @SuppressWarnings("unused")
    @OnClick(R.id.cl_short_info)
    public void shortInfoClick() {
        showFullInfo();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.tv_hide_details)
    public void hideDetailsClick() {
        showShortInfo();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_always_show_picture)
    public void alwaysShowPictureClicked() {
        CallSetEmailSafety callSetEmailSafety = new CallSetEmailSafety(new CallRequestResult() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onFail(ErrorType errorType, int serverCode) {
                RequestViewUtils.hideRequest();
                RequestViewUtils.showError(getActivity(), errorType, serverCode);
            }
        });
        callSetEmailSafety.setEmail( message.getFrom().getFirstEmail() );
        callSetEmailSafety.start();

       showPictureClicked();

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bt_show_picture)
    public void showPictureClicked() {
        showExternalPicture = true;
        bind();
        сlPictureBlocked.setVisibility(View.GONE);
    }


    private void showFullInfo() {
        clFullInfo.setVisibility(View.VISIBLE);
        clShortInfo.setVisibility(View.GONE);
    }

    private void showShortInfo() {
        clFullInfo.setVisibility(View.GONE);
        clShortInfo.setVisibility(View.VISIBLE);
    }




    @Override
    public FragmentWithRequestPermission onDownloadAttachmentClick(Attachments attachments) {
        return this;
    }

    private void openEncryptPopup() {


        String encryptedText = MessageUtils.convertToPlain(message.getPlain() );
        message.setPlain(encryptedText);

        EncryptDialogFragment encryptDialogFragment = new EncryptDialogFragment();
        encryptDialogFragment.setMessage(message);
        encryptDialogFragment.setEncryptCallback(this);
        encryptDialogFragment.setDecryptCallback(this);
        encryptDialogFragment.show(getActivity().getSupportFragmentManager(), "encryptDialogFragment");

    }



    private void verifyMessage() {
        VerifyTask verifyTask = new VerifyTask(message);
        verifyTask.setVerifyTaskCallback(new VerifyTask.VerifyTaskCallback() {
            @Override
            public void onSuccessVerify(String clearMessage) {
                Toast.makeText(MailViewFragment.this.getActivity(), getString(R.string.mail_view_verify_success), Toast.LENGTH_LONG).show();
                MailViewFragment.this.message.setPlain( clearMessage );
                MailViewFragment.this.message.setHtml ( clearMessage );
                bind();
            }

            @Override
            public void onFail(String description) {
                Toast.makeText(MailViewFragment.this.getActivity(), description, Toast.LENGTH_LONG).show();
            }
        });
        verifyTask.execute();
    }


    @Override
    public void onEncrypt(Message message, boolean encrypt, boolean sign) {
        this.message = message;
        bind();


        updateBottomMenu();
    }

    @Override
    public void onFail(String description) {
        //Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDecrypt(Message message) {
        this.message = message;
        bind();

        updateBottomMenu();
    }
    private void updateBottomMenu() {


        if (MessageUtils.isEncrypted(message)) {
            nvBottomMailView.setVisibility(View.VISIBLE);
            nvBottomMailView.getMenu().getItem(0).setTitle(getString(R.string.all_decrypt));
        }
        else  if (MessageUtils.isSigned(message)) {
            nvBottomMailView.setVisibility(View.VISIBLE);
            nvBottomMailView.getMenu().getItem(0).setTitle(getString(R.string.all_verify));
        }
        else {
            nvBottomMailView.setVisibility(View.GONE);
        }
    }
}
