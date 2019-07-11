package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Nonnull;

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

    public String getFolderName(@Nonnull FolderType folderType) {
        if (collection==null)
            return null;

        for (Folder folder: collection) {
            if (folder.getType() == folderType.getId()) {
                return folder.getFullNameRaw();
            }
        }
        return null;
    }

    public Folder getFolder(@Nonnull String name) {
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
