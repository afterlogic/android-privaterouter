package com.PrivateRouter.PrivateMail.network.logics;

import android.os.AsyncTask;

import com.PrivateRouter.PrivateMail.model.Folder;
import com.PrivateRouter.PrivateMail.model.FolderCollection;
import com.PrivateRouter.PrivateMail.model.FolderMeta;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.requests.CallGetFolder;
import com.PrivateRouter.PrivateMail.network.requests.CallGetFoldersMeta;
import com.PrivateRouter.PrivateMail.network.responses.GetFolderResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetFoldersMetaResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadFolderLogic  extends AsyncTask<Void, Void, Boolean>  {

    LoadFolderCallback onSuccessCallback;
    OnErrorInterface onFailCallback;

    FolderCollection folderCollection;


    public interface LoadFolderCallback {
        void onLoad(FolderCollection folderCollection);
    }

    public LoadFolderLogic(LoadFolderCallback onSuccessCallback, OnErrorInterface onFailCallback) {
        this.onSuccessCallback = onSuccessCallback;
        this.onFailCallback = onFailCallback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        CallGetFolder callGetFolder = new CallGetFolder(null);
        GetFolderResponse folderResponse = callGetFolder.startSync(onFailCallback);
        boolean success = folderResponse != null && folderResponse.getResult()!= null &&  folderResponse.getResult().getFolders()!=null;

        if (success) {
            folderCollection = folderResponse.getResult().getFolders();

            ArrayList<String> folders = new ArrayList<>();
            for (Folder folder: folderCollection.getCollection()) {
                folders.add(folder.getFullNameRaw() );
            }
            CallGetFoldersMeta callGetFoldersMeta = new CallGetFoldersMeta(null, folders);
            GetFoldersMetaResponse metaResponse = callGetFoldersMeta.startSync(onFailCallback);
            success = metaResponse!=null && metaResponse.getFolderMeta()!=null;

            if (success) {
                HashMap<String, FolderMeta> metaHashMap = metaResponse.getFolderMeta();
                for (Folder folder : folderCollection.getCollection() ) {
                    FolderMeta folderMeta = metaHashMap.get(folder.getName());
                    folder.setMeta( folderMeta );
                }
            }
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            if (onSuccessCallback!=null)
                onSuccessCallback.onLoad(folderCollection);
        }
        else {
            onFailCallback.onError(ErrorType.FAIL_CONNECT, "", 0);
        }

    }
}
