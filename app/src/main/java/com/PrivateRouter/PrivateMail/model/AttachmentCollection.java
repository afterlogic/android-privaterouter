package com.PrivateRouter.PrivateMail.model;

import android.arch.persistence.room.TypeConverters;

import com.PrivateRouter.PrivateMail.dbase.ArrayAttachmentConverter;
import com.PrivateRouter.PrivateMail.dbase.ArrayEmailConverter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AttachmentCollection implements Serializable {

    @SerializedName("@Count")
    private
    int count;

    @TypeConverters({ArrayAttachmentConverter.class})
    @SerializedName("@Collection")
    private
    ArrayList<Attachments> attachments;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Attachments> getAttachments() {
        return attachments;
    }


    public void setAttachments(ArrayList<Attachments> attachments) {
        this.attachments = attachments;
    }

    public Attachments find(String s) {

        if (attachments!=null) {
            for (Attachments attachments: attachments) {
                if (attachments.getCID().equals(s)) {
                    return attachments;
                }
            }
        }
        return null;
    }
}
