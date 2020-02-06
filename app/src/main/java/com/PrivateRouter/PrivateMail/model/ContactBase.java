package com.PrivateRouter.PrivateMail.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ContactBase implements Serializable {

    @NonNull
    @SerializedName("UUID")
    private
    String UUID;


    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }



    @SerializedName("ETag")
    private
    String eTag;

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}
