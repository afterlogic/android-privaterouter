package com.PrivateRouter.PrivateMail.view.mail_view;

import com.PrivateRouter.PrivateMail.model.Message;

public abstract class MailViewList {
    public abstract Message getMessage(int index);
    public abstract int getItemCount();
}
