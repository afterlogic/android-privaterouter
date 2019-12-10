package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.FolderCollection;
import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.google.gson.annotations.SerializedName;


public class GetFolderResponse extends BaseResponse {
    @SerializedName("Result")
    private
    Folder result;


    public Folder getResult() {
        return result;
    }

    public void setResult(Folder result) {
        this.result = result;
    }

    public class Folder {
        @SerializedName("Folders")
        private
        FolderCollection folders;
        @SerializedName("Namespace")
        private
        String namespace;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public FolderCollection getFolders() {
            return folders;
        }

        public void setFolders(FolderCollection folders) {
            this.folders = folders;
        }
    }
}