package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactSettings implements Serializable {
    @SerializedName("ContactsPerPage")
    private
    int contactsPerPage;

    @SerializedName("ImportContactsLink")
    private
    String importContactsLink;

    @SerializedName("Storages")
    private
    ArrayList<String> storages;

    @SerializedName("ImportExportFormats")
    private
    ArrayList<String> importExportFormats;

    public int getContactsPerPage() {
        return contactsPerPage;
    }

    public void setContactsPerPage(int contactsPerPage) {
        this.contactsPerPage = contactsPerPage;
    }

    public String getImportContactsLink() {
        return importContactsLink;
    }

    public void setImportContactsLink(String importContactsLink) {
        this.importContactsLink = importContactsLink;
    }

    public ArrayList<String> getStorages() {
        return storages;
    }

    public void setStorages(ArrayList<String> storages) {
        this.storages = storages;
    }

    public ArrayList<String> getImportExportFormats() {
        return importExportFormats;
    }

    public void setImportExportFormats(ArrayList<String> importExportFormats) {
        this.importExportFormats = importExportFormats;
    }
}
