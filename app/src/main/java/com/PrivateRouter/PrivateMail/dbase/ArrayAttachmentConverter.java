package com.PrivateRouter.PrivateMail.dbase;

import androidx.room.TypeConverter;

import com.PrivateRouter.PrivateMail.model.Attachments;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArrayAttachmentConverter {
    @TypeConverter
    public static ArrayList<Attachments> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Attachments>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Attachments> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
