package com.PrivateRouter.PrivateMail.network;

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

public class GetFolderMetaDeserializer implements JsonDeserializer<GetFoldersMetaResponse> {
    @Override
    public GetFoldersMetaResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Gson g = new Gson();

        GetFoldersMetaResponse response = null;

        try {
            response = g.fromJson(json, GetFoldersMetaResponse.class);
            HashMap<String, FolderMeta> responseMeta = new HashMap<>();

            JsonObject resultObj = obj.getAsJsonObject("Result");
            JsonObject countsObj = resultObj.getAsJsonObject( "Counts" );

            Set<Map.Entry<String, JsonElement>> entrySet = countsObj.entrySet();
            for(Map.Entry<String,JsonElement> entry : entrySet){
                String folderName = entry.getKey();
                JsonArray folderMetaObj = entry.getValue().getAsJsonArray();
                FolderMeta folderMeta = new FolderMeta();

                folderMeta.setCount( folderMetaObj.get(0).getAsInt() );
                folderMeta.setUnreadCount( folderMetaObj.get(1).getAsInt() );
                folderMeta.setNextUids( folderMetaObj.get(2).getAsString() );
                folderMeta.setHash( folderMetaObj.get(3).getAsString() );

                responseMeta.put(folderName, folderMeta);
            }

            response.setFolderMeta(responseMeta);


        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


        return response;
    }
}