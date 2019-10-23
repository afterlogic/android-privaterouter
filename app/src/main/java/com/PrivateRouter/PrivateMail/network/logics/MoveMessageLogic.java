package com.PrivateRouter.PrivateMail.network.logics;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.dbase.MessageDao;
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

public class MoveMessageLogic extends AsyncTask<Void, Void, Boolean> {

    private Runnable onSuccessCallback;
    private OnErrorInterface onFailCallback;
    MoveMessagesParameters moveMessagesParameters = new MoveMessagesParameters();
    Integer[] uids;

    public MoveMessageLogic(@NonNull String folder, @NonNull String folderTo,
                            Runnable onSuccessCallback, OnErrorInterface onFailCallback, @NonNull Integer ... uids ) {
        this.onFailCallback = onFailCallback;
        this.onSuccessCallback = onSuccessCallback;

        this.uids = uids;
        moveMessagesParameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();
        moveMessagesParameters.Folder = folder;
        moveMessagesParameters.ToFolder = folderTo;
        moveMessagesParameters.Uids = getUidsString(uids);
    }


    public String getUidsString(Integer ... uids) {
        String uidsString = "";
        for (Integer uid: uids ) {
            if (!uidsString.isEmpty())
                uidsString = uidsString + ", ";
            uidsString = uidsString +  uid;
        }
        return uidsString;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean success = request();
        if (success)
            success = deleteInCache();
        return success;
    }

    private boolean request() {
        Gson gson = new Gson();
        String json = gson.toJson(moveMessagesParameters);

        Call<BaseResponse> call= ApiFactory.getService().moveMessages(ApiModules.MAIL, ApiMethods.MOVE_MESSAGES, json );
        CallExecutor<BaseResponse> callExecutor = new CallExecutor<BaseResponse>();
        BaseResponse response = callExecutor.execute(call, onFailCallback);

        return response!=null;
    }

    private boolean deleteInCache() {
        try {
            AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
            MessageDao messageDao = database.messageDao();

            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (Integer uid: uids)
                arrayList.add(uid);

            messageDao.deletedFromList(moveMessagesParameters.Folder,  arrayList );

            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            if (onFailCallback != null)
                onFailCallback.onError(ErrorType.DB_ERROR, ex.getMessage(), 0);
        }
        return false;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            if (onSuccessCallback != null)
                onSuccessCallback.run();
        }
    }


    class MoveMessagesParameters {
        int AccountID;
        String Folder;
        String ToFolder;
        String Uids;
    }

}
