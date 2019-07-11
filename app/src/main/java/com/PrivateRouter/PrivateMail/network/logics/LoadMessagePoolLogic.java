package com.PrivateRouter.PrivateMail.network.logics;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Folder;
import com.PrivateRouter.PrivateMail.model.FolderHash;
import com.PrivateRouter.PrivateMail.model.FolderMeta;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.requests.CallGetFoldersMeta;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.network.responses.GetFoldersMetaResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.annotation.Nonnull;

public class LoadMessagePoolLogic extends AsyncTask<Void, Integer, Boolean> implements OnErrorInterface  {

    private static final String TAG = "LoadMessagePoolLogic";
    private ErrorType errorType = ErrorType.UNKNOWN;
    private int errorCode;
    private final CallRequestResult<Boolean> callback;
    private String currentFolderName;
    private boolean haveNewInCurrentFolder = false;
    private HashMap<String, FolderMeta> newFolderMeta;
    private boolean haveErrorLoadingFolder = false;
    private boolean forceCurrent = false;

    public  LoadMessagePoolLogic(@Nonnull String folder, @Nonnull CallRequestResult<Boolean> callback) {
        this.callback = callback;
        this.currentFolderName = folder;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.i(TAG, "start updating pool");
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        boolean success;

        success = updateFoldersMeta();
        if (!success  || isCancelled() ) return false;


        ArrayList<FolderToUpdate> updateList = getFolderListNeedBeUpdated();
        if ( updateList==null || isCancelled() ) return false;

        sortCurrentFolderToTop(updateList);


        for (FolderToUpdate folderToUpdate : updateList) {
            success = updateFolder(folderToUpdate.folderName, folderToUpdate.hash);
            if (!success  || isCancelled() ) return false;
        }

        Log.i(TAG, "finish updating pool");
        return !haveErrorLoadingFolder;


    }

    private void sortCurrentFolderToTop(ArrayList<FolderToUpdate> updateList) {
        Collections.sort(updateList, (folderToUpdate1, folderToUpdate2) -> {
            if( folderToUpdate1.folderName.equalsIgnoreCase(currentFolderName) && !folderToUpdate2.folderName.equalsIgnoreCase(currentFolderName) )
                return -1;
            else if ( !folderToUpdate1.folderName.equalsIgnoreCase(currentFolderName) && folderToUpdate2.folderName.equalsIgnoreCase(currentFolderName) )
                return 1;
            else return Integer.compare(folderToUpdate1.folderType, folderToUpdate2.folderType);
        });
    }

    private boolean updateFoldersMeta() {
        long currentTime = System.currentTimeMillis();
        Log.d(TAG, "updateFoldersMeta " +currentTime  );

        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        ArrayList<String> folders = new ArrayList<>();
        for (Folder folder: account.getFolders().getCollection()) {
            folders.add(folder.getFullNameRaw() );
        }


        CallGetFoldersMeta callGetFoldersMeta = new CallGetFoldersMeta(null, folders );
        GetFoldersMetaResponse response = callGetFoldersMeta.startSync(this);
        if (response!=null && response.getFolderMeta()!=null  ) {

            newFolderMeta = response.getFolderMeta();
            for (Folder folder : account.getFolders().getCollection() ) {
                FolderMeta newFolderMeta = this.newFolderMeta.get(folder.getName());
                folder.setMeta(newFolderMeta);
            }

            Context context = PrivateMailApplication.getContext();
            LoggedUserRepository.getInstance().save(context);

            return true;
        }
        else
            return false;

    }


    private ArrayList<FolderToUpdate> getFolderListNeedBeUpdated() {
        ArrayList<FolderToUpdate> arrayList = new ArrayList<FolderToUpdate>();

        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        for ( Folder folder:   account.getFolders().getCollection()) {
            if ( isCancelled() ) return null;

            String folderName = folder.getFullNameRaw();
            FolderMeta meta = newFolderMeta.get(folderName);
            if (meta!=null) {
                String newHash = meta.getHash();
                String oldHash = getFolderCurrentHash(folderName);

                Log.v(TAG, "folder = " + folderName + " newHash = " + newHash + " currentHash=" + oldHash);

                boolean forceCurrentFolder = folderName.equals(currentFolderName) && forceCurrent;

                if (!newHash.equals(oldHash) || forceCurrentFolder ) {

                    if (folder.getType() == FolderType.Inbox.getId() || folder.getType() == FolderType.Drafts.getId() ||
                            folder.getType() == FolderType.Sent.getId() || folderName.equals(currentFolderName) ) {

                        FolderToUpdate folderToUpdate = new FolderToUpdate();
                        folderToUpdate.folderName = folderName;
                        folderToUpdate.hash = newHash;
                        folderToUpdate.folderType = folder.getType();

                        arrayList.add(folderToUpdate);
                    }

                }
            }
        }
        return arrayList;
    }

    private boolean updateFolder(final String folderName, final  String newHash) {
        Log.i(TAG, "updateFolder "+folderName);
        LoadMessageLogic loadMessageLogic = new LoadMessageLogic(folderName);
        LoadMessageLogic.LoadMessageAnswer answer = loadMessageLogic.load();

        if (answer.success) {
            Log.i(TAG, "updateFolder "+folderName+ " success");
            saveNewFolderHash(folderName, newHash);

            if (answer.haveNewValue && folderName.equals(currentFolderName) )
                haveNewInCurrentFolder = true;

            return true;

        }
        else {
            Log.i(TAG, "updateFolder "+folderName+ " fail");
            LoadMessagePoolLogic.this.onError(answer.errorType, answer.errorCode);
            haveErrorLoadingFolder = true;
            return false;
        }

    }




    private void saveNewFolderHash(String folder, String hash) {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        FolderHash folderHash = new FolderHash();
        folderHash.setFolderHash(hash);
        folderHash.setFolderName(folder);

        database.messageDao().insertFolderHash(folderHash);
    }


    private String getFolderCurrentHash(String folder) {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        FolderHash folderHash = database.messageDao().getFolderHash(folder);

        String hash = "";
        if (folderHash!=null ) {
            hash = folderHash.getFolderHash();
        }
        return hash;
    }


    @Override
    public void onError(ErrorType errorType, int errorCode) {
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if ( isCancelled()) return;

        if (result) {
            callback.onSuccess( haveNewInCurrentFolder );
        }
        else {
            callback.onFail(errorType, errorCode);
        }

    }

    public void setForceCurrent(boolean forceCurrent) {
        this.forceCurrent = forceCurrent;
    }

    //TODO move load to parallel thread
    class UpdateFolderThread extends Thread {
        String folderName;
        String hash;

        public UpdateFolderThread(String folderName, String hash) {
            this.folderName = folderName;
            this.hash = hash;
        }

        public void run(){


        }
    }

    class FolderToUpdate {
        String hash;
        String folderName;
        int folderType;
    }
}
