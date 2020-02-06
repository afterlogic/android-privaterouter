package com.PrivateRouter.PrivateMail.model;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageInfo implements Serializable {

    @SerializedName("uid")
    private
    Integer uid;


    @SerializedName("flags")
    private
    ArrayList<String> flags;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public ArrayList<String> getFlags() {
        return flags;
    }

    public void setFlags(ArrayList<String> flags) {
        this.flags = flags;
    }
}
