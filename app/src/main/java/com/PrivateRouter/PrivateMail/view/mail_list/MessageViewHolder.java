package com.PrivateRouter.PrivateMail.view.mail_list;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.network.logics.AsyncThreadLoader;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.utils.CustomLinearLayoutManager;
import com.PrivateRouter.PrivateMail.view.utils.DateUtils;
import com.PrivateRouter.PrivateMail.view.utils.EmailUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.PrivateRouter.PrivateMail.PrivateMailApplication.getContext;

public class MessageViewHolder extends MailViewHolder {

    @BindView(R.id.tv_mail_messageSender)
    TextView tvMailMessageSender;

    @BindView(R.id.tv_mail_messageSubject)
    TextView tvMailMessageSubject;

    @BindView(R.id.tv_mail_messageDate)
    TextView tvMailMessageDate;

    @BindView(R.id.iv_mail_attachments)
    ImageView ivMailAttachments;

    @BindView(R.id.iv_threads_icon)
    ImageView ivThreadsIcon;

    @BindView(R.id.iv_mail_star)
    ImageView ivMailStar;

    @BindView(R.id.iv_mail_replied)
    ImageView ivMailReplied;

    @BindView(R.id.iv_mail_forwarded)
    ImageView ivMailForwarded;

    @BindView(R.id.rv_threads)
    RecyclerView rvThreads;

    @BindView(R.id.cb_selected)
    CheckBox cbSelected;


    boolean selectedMode;
    boolean checked;
    boolean expanded;
    boolean isSent;
    Message message;
    private MailListAdapter.OnMessageClick onMessageClick;
    private int position;
    private MailListAdapter mailListAdapter;
    boolean loadingThread = false;
    private boolean flatMode;

    @SuppressWarnings("unused")
    @OnClick(R.id.cl_background)
    public void bgImageClick() {
        if (selectedMode) {
            cbSelected.setChecked(!cbSelected.isChecked());
        } else {
            //Toast.makeText(cbSelected.getContext() , "uids: " + message.getUid() , Toast.LENGTH_SHORT).show();
            if (onMessageClick != null)
                onMessageClick.onMessageClick(message, position);
        }
    }

    @SuppressWarnings("unused")
    @OnLongClick(R.id.cl_background)
    public boolean bgImageLongClick() {
        mailListAdapter.onLongTap();
        return true;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_threads_icon)
    public void threadsIconClick() {
        if (loadingThread)
            return;
        expanded = !expanded;
        mailListAdapter.onExpandChange(expanded, message);
        updateThreads();
    }


    public void onCheckedChange(CompoundButton button, boolean value) {
        mailListAdapter.onSelectChange(value, message);
        if (!expanded && message.getThreadUidsList() != null && message.getThreadUidsList().size() > 0) {

            if (message.getThreadList() == null) {
                loadingThread = true;
                mailListAdapter.getMailListModeMediator().onStartLoadThread();
                AsyncThreadLoader asyncThreadLoader = new AsyncThreadLoader(message, this::onEndLoadThreadAndCheck);
                asyncThreadLoader.execute();
            } else {
                for (Message threadMessage : message.getThreadList()) {
                    mailListAdapter.onSelectChange(value, threadMessage);
                }
            }
        }
    }


    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }


    @SuppressWarnings("unused")
    @OnCheckedChanged(R.id.cb_selected)
    public void bgImageLongClick(boolean checked) {
        this.checked = checked;
    }

    public void bind(Message message, int position, boolean checked, boolean expanded, MailListAdapter mailListAdapter, boolean isSent) {
        this.checked = checked;
        this.isSent = isSent;
        this.expanded = expanded;
        this.position = position;
        this.message = message;
        this.mailListAdapter = mailListAdapter;

        this.loadingThread = false;

        if (message != null) {
            tvMailMessageSender.setVisibility(View.VISIBLE);
            tvMailMessageSubject.setVisibility(View.VISIBLE);
            tvMailMessageDate.setVisibility(View.VISIBLE);
            ivMailAttachments.setVisibility(View.VISIBLE);
            Context context = tvMailMessageDate.getContext();

            if (SettingsRepository.getInstance().isNightMode(context)) {
                ivThreadsIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_filter_none_white_24dp));

                DrawableCompat.setTint(ivMailReplied.getBackground(), ContextCompat.getColor(context.getApplicationContext(), R.color.color_white));
                DrawableCompat.setTint(ivMailForwarded.getBackground(), ContextCompat.getColor(context.getApplicationContext(), R.color.color_white));
                DrawableCompat.setTint(ivMailAttachments.getBackground(), ContextCompat.getColor(context.getApplicationContext(), R.color.color_white));

            } else {
                ivThreadsIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_filter_none_black_24dp));

                DrawableCompat.setTint(ivMailReplied.getBackground(), ContextCompat.getColor(context.getApplicationContext(), R.color.color_black));
                DrawableCompat.setTint(ivMailForwarded.getBackground(), ContextCompat.getColor(context.getApplicationContext(), R.color.color_black));
                DrawableCompat.setTint(ivMailAttachments.getBackground(), ContextCompat.getColor(context.getApplicationContext(), R.color.color_black));
            }


            cbSelected.setOnCheckedChangeListener(this::onCheckedChange);

            String topString;
            if (this.isSent) {
                topString = EmailUtils.getString(context, message.getTo());
            } else if (message.getFolder().equalsIgnoreCase(FolderType.Sent.name()) ||
                    message.getFolder().equalsIgnoreCase(FolderType.Drafts.name()))
                topString = EmailUtils.getString(context, message.getTo());
            else
                topString = EmailUtils.getString(context, message.getFrom());

            if (topString.equalsIgnoreCase(LoggedUserRepository.getInstance().getLogin()))
                topString = context.getString(R.string.all_me);

            tvMailMessageSender.setText(topString);

            String subject = message.getSubject();
            if (TextUtils.isEmpty(subject)) {
                subject = tvMailMessageSender.getContext().getString(R.string.mail_no_subject);
                tvMailMessageSubject.setAlpha(0.6f);// setTextColor( context.getResources().getColor(android.R.color.darker_gray ) );
            } else {
                tvMailMessageSubject.setAlpha(1f);//
                //tvMailMessageSubject.setTextColor( context.getResources().getColor(android.R.color.black) );
            }
            tvMailMessageSubject.setText(subject);


            String strDate = DateUtils.getShortDate(context, message.getTimeStampInUTC());
            tvMailMessageDate.setText(strDate);


            if (message.isHasAttachments())
                ivMailAttachments.setVisibility(View.VISIBLE);
            else
                ivMailAttachments.setVisibility(View.INVISIBLE);


            if (message.isFlagged())
                ivMailStar.setVisibility(View.VISIBLE);
            else
                ivMailStar.setVisibility(View.GONE);

            if (message.isForwarded())
                ivMailForwarded.setVisibility(View.VISIBLE);
            else
                ivMailForwarded.setVisibility(View.GONE);


            if (message.isAnswered())
                ivMailReplied.setVisibility(View.VISIBLE);
            else
                ivMailReplied.setVisibility(View.GONE);

            if (message.isSeen()) {
                tvMailMessageSender.setTypeface(null, Typeface.NORMAL);
                tvMailMessageSubject.setTypeface(null, Typeface.NORMAL);
                tvMailMessageDate.setTypeface(null, Typeface.NORMAL);
            } else {
                tvMailMessageSender.setTypeface(null, Typeface.BOLD);
                tvMailMessageSubject.setTypeface(null, Typeface.BOLD);
                tvMailMessageDate.setTypeface(null, Typeface.BOLD);
            }

            if (message.getThreadUidsList() != null && !message.getThreadUidsList().isEmpty() && !flatMode) {
                ivThreadsIcon.setVisibility(View.VISIBLE);
            } else {
                ivThreadsIcon.setVisibility(View.GONE);
            }

            if (!flatMode)
                updateThreads();

        } else {
            tvMailMessageSender.setText("");
            tvMailMessageSubject.setText("");
            tvMailMessageDate.setText("");

            tvMailMessageSender.setVisibility(View.INVISIBLE);
            tvMailMessageSubject.setVisibility(View.INVISIBLE);
            tvMailMessageDate.setVisibility(View.INVISIBLE);
            ivMailAttachments.setVisibility(View.INVISIBLE);
            ivMailStar.setVisibility(View.INVISIBLE);

            ivMailReplied.setVisibility(View.GONE);
            ivMailForwarded.setVisibility(View.GONE);
            cbSelected.setVisibility(View.GONE);
            rvThreads.setVisibility(View.GONE);
            ivThreadsIcon.setVisibility(View.GONE);
        }

    }

    private void updateThreads() {
        Context context = getContext();


        if (expanded) {
            rvThreads.setVisibility(View.VISIBLE);

            if (message.getThreadList() != null) {

                MessageListAdapter messageListAdapter = new MessageListAdapter(message.getThreadList(), mailListAdapter);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                rvThreads.setLayoutManager(mLayoutManager);
                rvThreads.setAdapter(messageListAdapter);

                CustomLinearLayoutManager customLayoutManager = new CustomLinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL, false);

                rvThreads.setLayoutManager(customLayoutManager);

            } else {
                loadingThread = true;
                mailListAdapter.getMailListModeMediator().onStartLoadThread();
                AsyncThreadLoader asyncThreadLoader = new AsyncThreadLoader(message, this::onEndLoadThread);
                asyncThreadLoader.execute();
            }

        } else {
            rvThreads.setVisibility(View.GONE);
        }

    }

    private void onEndLoadThreadAndCheck(Message message) {
        loadingThread = false;
        mailListAdapter.getMailListModeMediator().onEndLoadThread();

        for (Message threadMessage : message.getThreadList()) {
            mailListAdapter.onSelectChange(!checked, threadMessage);
        }
    }

    private void onEndLoadThread(Message message) {
        loadingThread = false;
        mailListAdapter.getMailListModeMediator().onEndLoadThread();
        updateThreads();
    }

    public MailListAdapter.OnMessageClick getOnMessageClick() {
        return onMessageClick;
    }

    public void setOnMessageClick(MailListAdapter.OnMessageClick onMessageClick) {
        this.onMessageClick = onMessageClick;
    }

    public void setSelectedMode(boolean selectedMode) {
        this.selectedMode = selectedMode;
        updateCheckBoxState();
    }

    private void updateCheckBoxState() {
        cbSelected.setChecked(checked);

        if (selectedMode)
            cbSelected.setVisibility(View.VISIBLE);
        else
            cbSelected.setVisibility(View.GONE);

    }

    public void setFlatMode(boolean flatMode) {
        this.flatMode = flatMode;
    }
}
