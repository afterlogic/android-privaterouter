package com.PrivateRouter.PrivateMail.creator;

import android.text.TextUtils;

import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.EmailCollection;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.utils.MessageUtils;

import java.util.ArrayList;

public class MessageCreator {
    protected Message message;
    public MessageCreator(Message message) {
        this.message = message;
    }

    protected void fillReplyTag(Message replyMessage, String replyTag) {
        ArrayList<String> draftInfo = new ArrayList<>(3);
        draftInfo.add(replyTag);
        draftInfo.add(""+message.getUid() );
        draftInfo.add(message.getFolder());
        replyMessage.setDraftInfo(draftInfo);
    }

    public void fillSender(Message replyMessage) {
        Account account = LoggedUserRepository.getInstance().getActiveAccount();


        EmailCollection senders = new EmailCollection();
        ArrayList<Email> sendersEmails = new ArrayList<Email>();
        Email email = new Email();
        email.setEmail( account.getEmail() );
        sendersEmails.add( email );
        senders.setEmails(sendersEmails);
        replyMessage.setSender(senders);
    }

    protected String getBaseMessage( ) {
        String baseMessage = "";
        if (!TextUtils.isEmpty(message.getHtml()) ) {
            baseMessage = MessageUtils.convertToPlain(message.getHtml());
        }
        else
            baseMessage = message.getPlain();

        if (baseMessage==null)
            baseMessage = "";

        return baseMessage;
    }
}
