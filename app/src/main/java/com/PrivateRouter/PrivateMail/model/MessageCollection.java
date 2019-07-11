package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MessageCollection {


    @SerializedName("@Count")
    private
    int count;

    @SerializedName("@Collection")
    private
    ArrayList<Message> collection;

    @SerializedName("Uids")
    private
    ArrayList<Integer> uids;

    @SerializedName("UidNext")
    private
    int uidNext;

    @SerializedName("FolderHash")
    private
    String folderHash;

    @SerializedName("MessageCount")
    private
    int messageCount;

    @SerializedName("MessageUnseenCount")
    private
    int messageUnseenCount;

    @SerializedName("MessageResultCount")
    private
    int messageResultCount;

    @SerializedName("FolderName")
    private
    String folderName;

    @SerializedName("Offset")
    private
    int offset;

    @SerializedName("Limit")
    private
    int limit;


    @SerializedName("Search")
    private
    String search;

    @SerializedName("Filters")
    private
    String filters;

    @SerializedName("New")
    private
    ArrayList<Object> newMesssages; //TODO

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Message> getCollection() {
        return collection;
    }

    public void setCollection(ArrayList<Message> collection) {
        this.collection = collection;
    }

    public ArrayList<Integer> getUids() {
        return uids;
    }

    public void setUids(ArrayList<Integer> uids) {
        this.uids = uids;
    }

    public int getUidNext() {
        return uidNext;
    }

    public void setUidNext(int uidNext) {
        this.uidNext = uidNext;
    }

    public String getFolderHash() {
        return folderHash;
    }

    public void setFolderHash(String folderHash) {
        this.folderHash = folderHash;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getMessageUnseenCount() {
        return messageUnseenCount;
    }

    public void setMessageUnseenCount(int messageUnseenCount) {
        this.messageUnseenCount = messageUnseenCount;
    }

    public int getMessageResultCount() {
        return messageResultCount;
    }

    public void setMessageResultCount(int messageResultCount) {
        this.messageResultCount = messageResultCount;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public ArrayList<Object> getNewMesssages() {
        return newMesssages;
    }

    public void setNewMesssages(ArrayList<Object> newMesssages) {
        this.newMesssages = newMesssages;
    }
}
