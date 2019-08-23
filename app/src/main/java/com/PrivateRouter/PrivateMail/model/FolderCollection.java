package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;

public class FolderCollection implements Serializable {
    @SerializedName("@Collection")
    private
    ArrayList<Folder> collection;

    public ArrayList<Folder> getCollection() {
        return collection;
    }

    public void setCollection(ArrayList<Folder> collection) {
        this.collection = collection;
    }

    public String getFolderName(@NonNull FolderType folderType) {
        if (collection==null)
            return null;

        for (Folder folder: collection) {
            if (folder.getType() == folderType.getId()) {
                return folder.getFullNameRaw();
            }
        }
        return null;
    }

    public Folder getFolder(@NonNull String name) {
        if (collection==null)
            return null;

        for (Folder folder: collection) {
            if (name.equals(folder.getFullName() ) ) {
                return folder;
            }
        }
        return null;
    }
}
