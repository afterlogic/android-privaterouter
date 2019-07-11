package com.PrivateRouter.PrivateMail.creator;

import android.content.Context;
import android.text.TextUtils;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.view.utils.DateUtils;

public class ForwardMessageCreator extends MessageCreator {


    public ForwardMessageCreator(Message message) {
        super(message);
    }

    public Message createForwardMessage(Context context) {

        Message forwardMessage = new Message();

        fillReplyTag(forwardMessage, "forward");

        fillMessageField(context, forwardMessage);

        fillSubject(forwardMessage);

        fillSender(forwardMessage);

        return forwardMessage;
    }



    private void fillSubject(Message forwardMessage) {
        forwardMessage.setSubject("Fwd: " + message.getSubject() );
    }

    private void fillMessageField(Context context, Message forwardMessage) {

        String baseMessage = getBaseMessage();
        String htmlMessage = "<br>";

        htmlMessage = htmlMessage + "---- Original Message ----<br />";

        if (message.getFrom()!=null) {
            String fromString = context.getString(R.string.all_from) + ": ";
            fromString = fromString + message.getFrom().getFirstEmail();
            fromString = fromString + "<br />";
            htmlMessage = htmlMessage + fromString;
        }

        if (message.getTo()!=null) {
            String toString = context.getString(R.string.all_to) + ": ";
            toString = toString + message.getTo().getEmailsString();
            toString = toString + "<br />";
            htmlMessage = htmlMessage + toString;
        }

        if (message.getCc()!=null) {
            String ccString = context.getString(R.string.all_cc) + ": ";
            ccString = ccString + message.getCc().getEmailsString();
            ccString = ccString + "<br />";
            htmlMessage = htmlMessage + ccString;
        }

        String dateString = context.getString(R.string.all_sent) + ": ";
        dateString = dateString + DateUtils.getFullDate(context, message.getTimeStampInUTC());
        dateString = dateString + "<br />";
        htmlMessage = htmlMessage + dateString;


        htmlMessage = htmlMessage + "Subject: "+ message.getSubject() + "<br />";


        htmlMessage = htmlMessage + baseMessage;

        forwardMessage.setPlain("");
        forwardMessage.setHtml(htmlMessage);
    }

}
