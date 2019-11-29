package com.PrivateRouter.PrivateMail.network.logics;

import android.os.AsyncTask;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.ContactBase;
import com.PrivateRouter.PrivateMail.model.Storages;
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

    private ArrayList<UUIDWithETag> uuidWithETagList;
    private ArrayList<String> uidsToUpdate;
    private ArrayList<String> uidsToRemove;
    private ArrayList<Contact> loadedContacts;
    private HashMap<String, ContactBase> currentContacts;

    private Boolean haveNew = false;
    private ErrorType errorType = ErrorType.UNKNOWN;
    private int errorCode;
    private String errorString;
    private List<Storages> newCTag;
    private List<Storages> oldCTag;


    public LoadContactPoolLogic(@NonNull CallRequestResult<Boolean> callback) {
        this.callback = callback;
    }


    private boolean equalsStorages(List<Storages> newCTag, List<Storages> oldCTag) {
        if (newCTag.size() != oldCTag.size()) {
            return false;
        }
        for (Storages storages : newCTag) {
            Storages thisStorages = null;
            for (Storages oldStorages : oldCTag) {
                if (storages.getId().equals(oldStorages.getId())) {
                    thisStorages = oldStorages;
                    break;
                }
            }
            if (thisStorages == null) {
                return false;
            }
            if (thisStorages.getCTag() != storages.getCTag()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        Logger.i(TAG, "start updating pool");
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        boolean success;

        success = getNewCTag();
        if (!success || isCancelled()) return false;

        success = loadCurrentCTag();
        if (!success || isCancelled()) return false;

        if (equalsStorages(oldCTag, newCTag))
            return true;

        success = cacheCTag();
        if (!success || isCancelled()) return false;

        success = getServerContactsUids();
        if (!success || isCancelled()) return false;


        success = loadCurrentContactTags();
        if (!success || isCancelled()) return false;

        success = getUidsNeedBeUpdatedAndRemoved();
        if (!success || isCancelled()) return false;

        success = requestNeedContacts();
        if (!success || isCancelled()) return false;

        success = insertNewContacts();
        if (!success || isCancelled()) return false;

        success = removeOldContacts();
        if (!success || isCancelled()) return false;

        success = cacheCTag();
        if (!success || isCancelled()) return false;


        return true;

    }


    @Override
    protected void onPostExecute(Boolean result) {
        if (isCancelled()) return;

        if (result) {
            callback.onSuccess(haveNew);
        } else {
            callback.onFail(errorType, errorString, errorCode);
        }

    }


    private boolean getNewCTag() {
        CallGetCTag callGetCTag = new CallGetCTag();

        newCTag = callGetCTag.syncStart(this);
        return newCTag != null;
    }


    private boolean loadCurrentCTag() {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();

        oldCTag = database.storagesDao().getAll();

        return true;

    }


    private boolean getServerContactsUids() {
        uuidWithETagList = new ArrayList<UUIDWithETag>();
        for (Storages storages : newCTag) {
            CallGetContactsInfo callGetContactsInfo = new CallGetContactsInfo();
            callGetContactsInfo.setStorage(storages.getId());
            ArrayList<UUIDWithETag> result = callGetContactsInfo.syncStart(this);
            if (result == null) {
                return false;
            }
            uuidWithETagList.addAll(result);
        }
        return true;
    }


    private boolean loadCurrentContactTags() {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        List<ContactBase> currentContactsList = database.messageDao().getStorageContacts();
        currentContacts = new HashMap<>();
        for (ContactBase contactBase : currentContactsList) {
            currentContacts.put(contactBase.getUUID(), contactBase);
        }

        return true;
    }

    private boolean getUidsNeedBeUpdatedAndRemoved() {
        uidsToUpdate = new ArrayList<>();
        uidsToRemove = new ArrayList<>();

        for (UUIDWithETag uuidWithETag : uuidWithETagList) {
            if (isCancelled()) return false;

            ContactBase existContact = currentContacts.get(uuidWithETag.getUUID());
            if (existContact == null || existContact.getETag() == null || !existContact.getETag().equals(uuidWithETag.getETag())) {
                uidsToUpdate.add(uuidWithETag.getUUID());
                haveNew = true;

            }

            if (existContact != null)
                currentContacts.remove(uuidWithETag.getUUID());

        }


        for (String uuid : currentContacts.keySet()) {
            if (isCancelled()) return false;
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

        for (Contact contact : loadedContacts) {

            if (isCancelled()) return false;
            insertContact(contact);
        }
        return true;
    }

    private boolean removeOldContacts() {

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        database.messageDao().clearOldContacts(uidsToRemove);

        return true;
    }


    private void insertContact(Contact contact) {

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        database.messageDao().insertContact(contact);
    }


    private boolean cacheCTag() {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();

        for (Storages storages : newCTag) {
            database.storagesDao().update(storages);
        }

        return true;
    }


    @Override
    public void onError(ErrorType errorType, String errorString, int errorCode) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorString = errorString;
    }
}
