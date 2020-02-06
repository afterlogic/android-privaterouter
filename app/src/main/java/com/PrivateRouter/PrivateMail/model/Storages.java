package com.PrivateRouter.PrivateMail.model;

import androidx.room.Entity;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "storages", primaryKeys = {"id"})
public class Storages implements Serializable {
    @SerializedName("Id")
    @NonNull
    private String id;
    @SerializedName("Name")
    private String name;
    @SerializedName("CTag")
    private int cTag;
    @SerializedName("Display")
    private boolean display=true;

    public Storages(String id, int cTag, boolean display) {
        this.id = id;
        this.cTag = cTag;
        this.display = display;
    }

    public boolean getDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getId() {
        return id;
    }

    public int getCTag() {
        return cTag;
    }

    public void setCTag(int cTag) {
        this.cTag = cTag;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        if (name == null) {
            return id;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
