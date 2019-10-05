package com.PrivateRouter.PrivateMail.view.mail_view;

import com.PrivateRouter.PrivateMail.model.Message;

import java.io.Serializable;

public abstract class MailViewList  implements Serializable {
    public abstract Message getMessage(int index);
    public abstract int getItemCount();
}
