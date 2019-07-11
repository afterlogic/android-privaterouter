package com.PrivateRouter.PrivateMail.model;

import android.arch.persistence.room.TypeConverters;

import com.PrivateRouter.PrivateMail.dbase.ArrayAttachmentConverter;
import com.PrivateRouter.PrivateMail.dbase.ArrayEmailConverter;
import com.PrivateRouter.PrivateMail.dbase.ArrayStringConverter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EmailCollection implements Serializable {
    @SerializedName("@Count")
    private
    int count;

    @TypeConverters({ArrayEmailConverter.class})
    @SerializedName("@Collection")
    private
    ArrayList<Email> emails;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Email> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
    }

    public String getFirstEmail() {
        if (emails!=null && !emails.isEmpty()) {
            return emails.get(0).getEmail();
        }

        return null;
    }

    public String getEmailsString() {
        if (emails!=null && !emails.isEmpty()) {
            String str = "";
            for (Email email: emails) {
                if (!str.isEmpty())
                    str = str + ", ";
                str =str + email.getEmail();
            }
            return str;
        }
        return "";
    }
}
