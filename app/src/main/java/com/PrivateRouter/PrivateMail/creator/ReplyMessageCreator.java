package com.PrivateRouter.PrivateMail.creator;

import android.content.Context;
import android.text.TextUtils;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.EmailCollection;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.utils.MessageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReplyMessageCreator extends MessageCreator{


    public ReplyMessageCreator(Message message) {
        super(message);
    }

    public Message createReplyMessage(Context context, boolean replyAll) {

        Message replyMessage = new Message();

        if (replyAll)
            fillReplyTag(replyMessage, "reply-all");
        else
            fillReplyTag(replyMessage, "reply");


        fillToField(replyMessage);

        fillMessageField(context, replyMessage);

        fillSubject(replyMessage);

        fillSender(replyMessage);

        if (replyAll)
            fillCcField(replyMessage);



        return replyMessage;
    }


    private void fillCcField(Message replyMessage) {

        EmailCollection emailCollection = new EmailCollection();
        if (message.getCc()!=null)
            emailCollection.setEmails(message.getCc().getEmails());

        if (emailCollection.getEmails() == null)
            emailCollection.setEmails( new ArrayList<>());

        Account account = LoggedUserRepository.getInstance().getActiveAccount();

        if (message.getTo() != null && message.getTo().getEmails()!=null) {
            for (Email email : message.getTo().getEmails()) {
                if (!TextUtils.isEmpty(email.getEmail()) && !email.getEmail().equals( account.getEmail() ) ) {
                    emailCollection.getEmails().add(email);
                }
            }
        }

        replyMessage.setCc(emailCollection);
    }

    private void fillMessageField(Context context, Message replyMessage) {

        String baseMessage = getBaseMessage();


        String htmlReplyMessage = "<html><head></head><body><br>";



        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm a", Locale.US);
        String dateStr =   simpleDateFormat.format(date);


        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        String senderStr = "<a href=\"mailto:" + account.getEmail() + "\" target=\"_blank\" tabindex=\"-1\" rel=\"external\">"+
                account.getEmail()+"</a>";

        htmlReplyMessage = htmlReplyMessage+ String.format(context.getString(R.string.message_on_wrote) ,dateStr, senderStr);

        if (!TextUtils.isEmpty(baseMessage)) {
            htmlReplyMessage = htmlReplyMessage + "<style blockquote {border-left: solid 2px #000000; margin: 4px 2px; padding-left: 6px; }></style>";
            htmlReplyMessage = htmlReplyMessage + "<br><blockquote><div xmlns=\"http://www.w3.org/1999/xhtml\">";

            htmlReplyMessage = htmlReplyMessage + baseMessage;

            htmlReplyMessage = htmlReplyMessage + "</div></blockquote><br>";

        }

        htmlReplyMessage = htmlReplyMessage + "</body></html>";




        replyMessage.setPlain( "" );
        replyMessage.setHtml( htmlReplyMessage );

    }



    private void fillSubject(Message replyMessage) {

        replyMessage.setSubject("Re: " + message.getSubject() );
    }

    private void fillToField(Message replyMessage) {

        EmailCollection emailCollectionReference = null;
        if (message.getReplyTo()!=null)
            emailCollectionReference = message.getReplyTo();
        else if (message.getFrom()!=null)
            emailCollectionReference = message.getFrom();

        if (emailCollectionReference!=null && emailCollectionReference.getEmails()!=null) {
            EmailCollection emailCollectionTo = new EmailCollection();
            ArrayList<Email> emails = new ArrayList<>(emailCollectionReference.getEmails().size());
            for (Email emailRef : emailCollectionReference.getEmails()) {
                Email email = new Email();
                email.setEmail(emailRef.getEmail());
                email.setDisplayName(emailRef.getDisplayName());
                emails.add(email);
            }
            emailCollectionTo.setEmails(emails);
            replyMessage.setTo(emailCollectionTo);
        }
    }



}
