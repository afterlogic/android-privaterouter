package com.PrivateRouter.PrivateMail.model;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "temp_message_ids", primaryKeys = {"ids"} )
public class TempMessageIds {

    @SerializedName("ids")
    private
    long ids;

    public long getIds() {
        return ids;
    }

    public void setIds(long ids) {
        this.ids = ids;
    }
}
