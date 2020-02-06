package com.PrivateRouter.PrivateMail.view.mail_list;

import androidx.annotation.NonNull;
import android.view.View;

import com.PrivateRouter.PrivateMail.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MailViewBarHolder extends MailViewHolder {
    private MailListModeMediator mailListModeMediator;

    public MailViewBarHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cl_background)
    public void bgShowMoreClick() {
        mailListModeMediator.onLoadMoreClick();
    }

    public void bind(MailListModeMediator mailListModeMediator) {
        this.mailListModeMediator = mailListModeMediator;
    }
}
