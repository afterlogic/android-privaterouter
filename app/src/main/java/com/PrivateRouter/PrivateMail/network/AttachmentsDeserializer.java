package com.PrivateRouter.PrivateMail.network;

import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.model.FolderMeta;
import com.PrivateRouter.PrivateMail.network.responses.GetFoldersMetaResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AttachmentsDeserializer implements JsonDeserializer<Attachments> {
    @Override
    public Attachments deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Gson g = new Gson();

        Attachments attachments = null;

        try {
            attachments = g.fromJson(json, Attachments.class);

            boolean hasSize = obj.has("Size");
            if (hasSize) {
                attachments.setEstimatedSize(obj.get("Size").getAsInt());
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


        return attachments;
    }
}