package com.PrivateRouter.PrivateMail.network.logics;

import android.os.AsyncTask;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.ContactBase;
import com.PrivateRouter.PrivateMail.model.StorageCTag;
import com.PrivateRouter.PrivateMail.model.UUIDWithETag;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.requests.CallGetCTag;
import com.PrivateRouter.PrivateMail.network.requests.CallGetContacts;
import com.PrivateRouter.PrivateMail.network.requests.CallGetContactsInfo;
import com.PrivateRouter.PrivateMail.network.requests.CallRequestResult;
import com.PrivateRouter.PrivateMail.view.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

public class LoadContactPoolLogic extends AsyncTask<Void, Integer, Boolean> implements OnErrorInterface {

    private static final String TAG = "LoadContactPoolLogic";
    private final CallRequestResult<Boolean> callback;
    private String currentStorage;
    private ArrayList<UUIDWithETag> uuidWithETagList;
    private ArrayList<String> uidsToUpdate;
    private ArrayList<String> uidsToRemove;
    private ArrayList<Contact>  loadedContacts;
    private HashMap<String, ContactBase> currentContacts;

    private Boolean haveNew = false;
    private ErrorType errorType = ErrorType.UNKNOWN;
    private int errorCode;
    private String errorString;
    private int newCTag;
    private int oldCTag;


    public  LoadContactPoolLogic(@NonNull String storage, @NonNull CallRequestResult<Boolean> callback) {
        this.callback = callback;
        this.currentStorage = storage;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        Logger.i(TAG, "start updating pool");
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        boolean success;

        success = getNewCTag();
        if (!success  || isCancelled() ) return false;

        success = loadCurrentCTag();
        if (!success  || isCancelled() ) return false;

        if (oldCTag == newCTag)
            return true;

        success = getServerContactsUids();
        if (!success  || isCancelled() ) return false;


        success = loadCurrentContactTags();
        if (!success  || isCancelled() ) return false;

        success = getUidsNeedBeUpdatedAndRemoved();
        if (!success  || isCancelled() ) return false;

        success = requestNeedContacts();
        if (!success  || isCancelled() ) return false;

        success = insertNewContacts();
        if (!success  || isCancelled() ) return false;

        success = removeOldContacts();
        if (!success  || isCancelled() ) return false;

        success = cacheCTag();
        if (!success  || isCancelled() ) return false;



        return true;

    }


    @Override
    protected void onPostExecute(Boolean result) {
        if ( isCancelled()) return;

        if (result) {
            callback.onSuccess( haveNew );
        }
        else {
            callback.onFail(errorType, errorString, errorCode);
        }

    }



    private boolean getNewCTag() {
        CallGetCTag callGetCTag = new CallGetCTag();
        callGetCTag.setStorage(currentStorage);
        newCTag = callGetCTag.syncStart(this);
        return newCTag != CallGetCTag.FAIL;
    }


    private boolean loadCurrentCTag() {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        StorageCTag storageCTag = database.messageDao().getStorageCTag(currentStorage);

        oldCTag = CallGetCTag.FAIL;
        if (storageCTag!=null ) {
            oldCTag = storageCTag.getCTag();

        }
        return true;

    }



    private boolean getServerContactsUids() {
        CallGetContactsInfo callGetContactsInfo = new CallGetContactsInfo();
        callGetContactsInfo.setStorage(currentStorage);
        uuidWithETagList = callGetContactsInfo.syncStart(this);
        return uuidWithETagList != null;
    }


    private boolean loadCurrentContactTags() {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        List<ContactBase> currentContactsList = database.messageDao().getStorageContactsUids(currentStorage);
        currentContacts = new HashMap<>();
        for (ContactBase contactBase: currentContactsList) {
            currentContacts.put(contactBase.getUUID(), contactBase);
        }

        return true;
    }

    private boolean getUidsNeedBeUpdatedAndRemoved() {
        uidsToUpdate = new ArrayList<>();
        uidsToRemove = new ArrayList<>();

        for (UUIDWithETag uuidWithETag : uuidWithETagList )  {
            if (  isCancelled() ) return false;

            ContactBase existContact =  currentContacts.get( uuidWithETag.getUUID() );
            if (existContact == null || existContact.getETag() == null || !existContact.getETag().equals(uuidWithETag.getETag()) ) {
                uidsToUpdate.add( uuidWithETag.getUUID() );
                haveNew = true;

            }

            if (existContact!=null)
                currentContacts.remove( uuidWithETag.getUUID() );

        }


        for (String uuid: currentContacts.keySet() )  {
            if (  isCancelled() ) return false;
            uidsToRemove.add(uuid);

        }
        currentContacts = null;

        return true;
    }


    private boolean requestNeedContacts() {
        CallGetContacts callGetContacts = new CallGetContacts();
        callGetContacts.setUids(uidsToUpdate);
        loadedContacts = callGetContacts.syncStart(this);
        return loadedContacts != null;
    }


    private boolean insertNewContacts() {

        for (Contact contact: loadedContacts )  {

            if (  isCancelled() ) return false;
            insertContact(contact);
        }
        return true;
    }

    private boolean removeOldContacts() {

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        database.messageDao().clearOldContacts( uidsToRemove );

        return true;
    }





    private void insertContact(Contact contact) {

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        database.messageDao().insertContact( contact);
    }


    private boolean cacheCTag() {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        StorageCTag storageCTag = new StorageCTag();
        storageCTag.setCTag(newCTag);
        storageCTag.setStorage(currentStorage);

        database.messageDao().insertStorageCTag(storageCTag);
        return true;
    }



    @Override
    public void onError(ErrorType errorType, String errorString, int errorCode) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorString = errorString;
    }
}
