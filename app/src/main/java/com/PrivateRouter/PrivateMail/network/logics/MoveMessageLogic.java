package com.PrivateRouter.PrivateMail.network.logics;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.dbase.MessageDao;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Folder;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.requests.CallExecutor;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;

public class MoveMessageLogic extends AsyncTask<Void, Void, ErrorType> {

    private Runnable onSuccessCallback;
    private OnErrorInterface onFailCallback;
    MoveMessagesParameters moveMessagesParameters = new MoveMessagesParameters();
    Integer[] uids;

    public MoveMessageLogic(@NonNull String folder, @NonNull FolderType folderToType,
                            Runnable onSuccessCallback, OnErrorInterface onFailCallback, @NonNull Integer... uids) {
        this.onFailCallback = onFailCallback;
        this.onSuccessCallback = onSuccessCallback;

        this.uids = uids;
        moveMessagesParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();
        moveMessagesParameters.Folder = folder;
        moveMessagesParameters.type = folderToType;
        moveMessagesParameters.Uids = getUidsString(uids);
    }


    public String getUidsString(Integer... uids) {
        String uidsString = "";
        for (Integer uid : uids) {
            if (!uidsString.isEmpty())
                uidsString = uidsString + ", ";
            uidsString = uidsString + uid;
        }
        return uidsString;
    }


    @Override
    protected ErrorType doInBackground(Void... voids) {
        ErrorType success = request();
        if (success == null)
            success = deleteInCache();
        return success;
    }

    private ErrorType request() {
        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        Folder folder = account.getFolders().getFolder(moveMessagesParameters.type);
        moveMessagesParameters.ToFolder =  folder.getFullNameRaw();
        moveMessagesParameters.type = null;
        Gson gson = new Gson();
        String json = gson.toJson(moveMessagesParameters);

        Call<BaseResponse> call = ApiFactory.getService().moveMessages(ApiModules.MAIL, ApiMethods.MOVE_MESSAGES, json);
        CallExecutor<BaseResponse> callExecutor = new CallExecutor<BaseResponse>();
        BaseResponse response = null;
        try {
            response = callExecutor.execute(call, onFailCallback);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return response != null ? null : ErrorType.FAIL_CONNECT;
    }

    private ErrorType deleteInCache() {
        try {
            AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
            MessageDao messageDao = database.messageDao();

            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (Integer uid : uids)
                arrayList.add(uid);

            messageDao.deletedFromList(moveMessagesParameters.Folder, arrayList);

            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ErrorType.DB_ERROR;
        }
    }


    @Override
    protected void onPostExecute(ErrorType error) {
        if (error == null) {
            if (onSuccessCallback != null)
                onSuccessCallback.run();
        } else {
            if (onFailCallback != null)
                onFailCallback.onError(error, "", 0);
        }
    }


    class MoveMessagesParameters {
        int AccountID;
        String Folder;
        String ToFolder;
        FolderType type;
        String Uids;
    }

}
