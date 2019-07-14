package com.PrivateRouter.PrivateMail.network;

import com.PrivateRouter.PrivateMail.model.ContactSettings;
import com.PrivateRouter.PrivateMail.model.NamedEnums;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class ContactSettingsDeserializer  implements JsonDeserializer<ContactSettings> {
    @Override
    public ContactSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Gson g = new Gson();

        ContactSettings settings = null;

        try {
            settings = g.fromJson(json, ContactSettings.class);

            ArrayList<NamedEnums> primaryEmail = new ArrayList<NamedEnums>();
            JsonObject primaryEmailObject =  obj.getAsJsonObject("PrimaryEmail");
            for (Map.Entry<String, JsonElement> elementEntry : primaryEmailObject.entrySet()) {
                String key = elementEntry.getKey();
                int value = elementEntry.getValue().getAsInt();
                NamedEnums enums = new NamedEnums();
                enums.setName(key);
                enums.setId(value);
                primaryEmail.add(enums);
            }
            settings.setPrimaryEmail(primaryEmail);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


        return settings;
    }
}