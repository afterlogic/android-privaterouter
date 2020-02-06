package com.PrivateRouter.PrivateMail.model;

import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(tableName = "folder_hash", primaryKeys = {"folderName" }  )
public class FolderHash {
    @NonNull
    private String folderName;


    private String folderHash;

    @NonNull
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(@NonNull String folderName) {
        this.folderName = folderName;
    }

    public String getFolderHash() {
        return folderHash;
    }

    public void setFolderHash(String folderHash) {
        this.folderHash = folderHash;
    }
}
