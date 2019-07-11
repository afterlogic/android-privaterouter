package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.FolderMeta;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class GetFoldersMetaResponse extends BaseResponse{

    private
    HashMap<String, FolderMeta> foldersMeta;

    public HashMap<String, FolderMeta> getFolderMeta() {
        return foldersMeta;
    }

    public void setFolderMeta(HashMap<String, FolderMeta>  foldersMeta) {
        this.foldersMeta = foldersMeta;
    }



}