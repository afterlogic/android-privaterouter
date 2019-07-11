package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UUIDWithETag implements Serializable {

    @SerializedName("UUID")
    private
    String uUId;

    @SerializedName("ETag")
    private
    String eTag;

    public String getUUID() {
        return uUId;
    }

    public void setUUID(String uUId) {
        this.uUId = uUId;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}
