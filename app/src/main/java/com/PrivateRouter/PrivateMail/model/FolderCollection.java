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
        if (collection == null)
            return null;

        for (Folder folder : collection) {
            if (folder.getType() == folderType.getId()) {
                return folder.getFullNameRaw();
            }
        }
        return null;
    }

    public Folder getFolder(@NonNull String name) {

        return getFolderInCollection(name, collection);
    }

    public Folder getFolderInCollection(@NonNull String name, ArrayList<Folder> collection) {
        if (collection == null || collection.isEmpty())
            return null;

        for (Folder folder : collection) {
            if (name.equals(folder.getFullName())) {
                return folder;
            }
            if (folder.getSubFolders() != null) {
                Folder retFolder = getFolderInCollection(name, folder.getSubFolders().collection);
                if (retFolder != null)
                    return retFolder;
            }
        }
        return null;
    }

}
