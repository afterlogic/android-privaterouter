package com.PrivateRouter.PrivateMail.network;

import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MessageBaseDeserializer implements JsonDeserializer<MessageBase>  {
    @Override
    public MessageBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String uidsString = obj.get("uid").getAsString();

        MessageBase messageBase = new MessageBase();
        messageBase.setUid( Integer.parseInt(uidsString));
        JsonArray threadList = obj.getAsJsonArray("thread");
        if ( threadList!=null ) {

            ArrayList<MessageBase> messageThreads = new ArrayList<>();
            for (int i = 0; i < threadList.size(); i++) {
                JsonElement jsonElement = threadList.get(i);
                MessageBase messageBaseThreadElement = context.deserialize(jsonElement,  MessageBase.class);
                messageThreads.add( messageBaseThreadElement  );
            }

            messageBase.setThread(messageThreads);
        }

        JsonArray flags = obj.get("flags").getAsJsonArray();
        for (int i = 0; i< flags.size(); i++) {
            String flag = flags.get(i).getAsString();

            if (flag.equals("\\seen"))
                messageBase.setSeen(true);
            else if (flag.equals("\\answered"))
                messageBase.setAnswered(true);
            else if (flag.equals("\\flagged"))
                messageBase.setFlagged(true);
            else if (flag.equals("\\deleted"))
                messageBase.setDeleted(true);
            else  if (flag.equals("\\draft"))
                messageBase.setDraft(true);
            else if (flag.equals("\\recent"))
                messageBase.setRecent(true);
            else if (flag.equals("$forwarded"))
                messageBase.setForwarded(true);

        }

        return messageBase;
    }
}
