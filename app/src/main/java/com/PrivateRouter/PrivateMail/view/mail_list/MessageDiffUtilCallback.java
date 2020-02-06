package com.PrivateRouter.PrivateMail.view.mail_list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.PrivateRouter.PrivateMail.model.Message;

public class MessageDiffUtilCallback extends DiffUtil.ItemCallback<Message> {


    @Override
    public boolean areItemsTheSame(@NonNull Message message1, @NonNull Message message2) {
        return message1.getMessageId().equals(message2.getMessageId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Message message1, @NonNull Message message2) {
        boolean equalID = message1.getMessageId().equals(message2.getMessageId());
        if (equalID) {

            boolean threadsEquals = ((message1.getThreadList() ==  null) && (message2.getThreadList() == null) ) ||
                                    ((message1.getThreadList() !=  null) && (message2.getThreadList() != null) && message1.getThreadList().size() == message2.getThreadList().size() );


            return  (message1.isSeen() == message2.isSeen()) &&
                    (message1.isFlagged() == message2.isFlagged() &&
                     threadsEquals );
        }
        return false;
    }
}