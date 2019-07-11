package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Folder implements Serializable {

    @SerializedName("Type")
    private
    int type;

    @SerializedName("Name")
    private
    String name;

    @SerializedName("FullName")
    private
    String fullName;

    @SerializedName("FullNameRaw")
    private
    String fullNameRaw;

    @SerializedName("FullNameHash")
    private
    String fullNameHash;

    @SerializedName("Delimiter")
    private
    String delimiter;

    @SerializedName("IsSubscribed")
    private
    boolean isSubscribed;

    @SerializedName("IsSelectable")
    private
    boolean isSelectable;

    @SerializedName("Exists")
    private
    boolean exists;

    //TODO  "Extended"

    @SerializedName("AlwaysRefresh")
    private
    boolean alwaysRefresh;

    @SerializedName("SubFolders")
    private
    FolderCollection subFolders;

    private
    int level;

    private
    FolderMeta meta;



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullNameRaw() {
        return fullNameRaw;
    }

    public void setFullNameRaw(String fullNameRaw) {
        this.fullNameRaw = fullNameRaw;
    }

    public String getFullNameHash() {
        return fullNameHash;
    }

    public void setFullNameHash(String fullNameHash) {
        this.fullNameHash = fullNameHash;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public boolean isAlwaysRefresh() {
        return alwaysRefresh;
    }

    public void setAlwaysRefresh(boolean alwaysRefresh) {
        this.alwaysRefresh = alwaysRefresh;
    }

    public FolderCollection getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(FolderCollection subFolders) {
        this.subFolders = subFolders;
    }

    public FolderMeta getMeta() {
        return meta;
    }

    public void setMeta(FolderMeta folderMeta) {
        this.meta = folderMeta;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

