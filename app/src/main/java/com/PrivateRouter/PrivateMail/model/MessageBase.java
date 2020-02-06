package com.PrivateRouter.PrivateMail.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import com.PrivateRouter.PrivateMail.dbase.ArrayIntegerConverter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageBase implements Serializable {

    @NonNull
    @SerializedName("Uid")
    private
    int uid;

    @SerializedName("IsSeen")
    private
    boolean isSeen;

    @SerializedName("IsAnswered")
    private
    boolean isAnswered;


    @SerializedName("IsFlagged")
    private
    boolean isFlagged;


    @SerializedName("IsDeleted")
    private
    boolean isDeleted;

    @SerializedName("IsDraft")
    private
    boolean isDraft;


    @SerializedName("IsRecent")
    private
    boolean isRecent;


    @SerializedName("IsForwarded")
    private
    boolean isForwarded;

    @Ignore
    @SerializedName("thread")
    private
    ArrayList<MessageBase> thread;


    @TypeConverters({ArrayIntegerConverter.class})
    @SerializedName("threadUidsList")
    private
    ArrayList<Integer> threadUidsList;

    @SerializedName("parentUid")
    private
    int parentUid;



    public boolean equals(Object object) {
        if (object instanceof  MessageBase) {
            MessageBase messageBase = (MessageBase) object;

            boolean threadsEquals = ((messageBase.getThreadUidsList() ==  null) && (getThreadUidsList() == null) ) ||
                    ((messageBase.getThreadUidsList() !=  null) && (getThreadUidsList() != null) && messageBase.getThreadUidsList().size() == getThreadUidsList().size() );


            return  (messageBase.getUid() == getUid() ) &&
                    (messageBase.isSeen() == isSeen()) &&
                    (messageBase.isAnswered() == isAnswered()) &&
                    (messageBase.isFlagged() == isFlagged()) &&
                    (messageBase.isDeleted() == isDeleted()) &&
                    (messageBase.isDraft() == isDraft()) &&
                    (messageBase.isRecent() == isRecent()) &&
                    (messageBase.isForwarded() == isForwarded()) &&
                    (messageBase.getParentUid() == getParentUid()) && threadsEquals;


        }
        return false;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public boolean isForwarded() {
        return isForwarded;
    }

    public void setForwarded(boolean forwarded) {
        isForwarded = forwarded;
    }

    public boolean isRecent() {
        return isRecent;
    }

    public void setRecent(boolean recent) {
        isRecent = recent;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public ArrayList<MessageBase> getThread() {
        return thread;
    }

    public void setThread(ArrayList<MessageBase> thread) {
        this.thread = thread;
    }

    public ArrayList<Integer> getThreadUidsList() {
        return threadUidsList;
    }

    public void setThreadUidsList(ArrayList<Integer> threadUidsList) {
        this.threadUidsList = threadUidsList;
    }

    public int getParentUid() {
        return parentUid;
    }

    public void setParentUid(int parentUid) {
        this.parentUid = parentUid;
    }
}
