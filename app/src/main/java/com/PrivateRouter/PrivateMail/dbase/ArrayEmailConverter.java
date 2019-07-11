package com.PrivateRouter.PrivateMail.dbase;

import android.arch.persistence.room.TypeConverter;

import com.PrivateRouter.PrivateMail.model.Email;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArrayEmailConverter {
    @TypeConverter
    public static ArrayList<Email> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Email>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Email> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
