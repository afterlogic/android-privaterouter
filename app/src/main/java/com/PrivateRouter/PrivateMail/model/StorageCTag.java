package com.PrivateRouter.PrivateMail.model;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;


@Entity(tableName = "storage_c_tag", primaryKeys = {"storage" }  )
public class StorageCTag {
    @NonNull
    private String storage;


    private int cTag;

    @NonNull
    public String getStorage() {
        return storage;
    }

    public void setStorage(@NonNull String storage) {
        this.storage = storage;
    }

    public int getCTag() {
        return cTag;
    }

    public void setCTag(int cTag) {
        this.cTag = cTag;
    }


}
