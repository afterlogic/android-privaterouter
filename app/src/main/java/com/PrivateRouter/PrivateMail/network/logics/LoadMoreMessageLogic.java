package com.PrivateRouter.PrivateMail.network.logics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.requests.CallGetMessagesBase;
import com.google.gson.Gson;

import java.util.List;

public class LoadMoreMessageLogic extends AsyncTask<Void, Integer, Boolean>   {

    private static final String PREF_NAME = "MoreMessageIterator";
    private final LoadMoreCallback loadMoreCallback;
    private int errorCode;
    private ErrorType errorType;
    private boolean haveNewValue = false;
    private String folder;
    private String errorString;

    public interface  LoadMoreCallback {
        void onSuccess(boolean hasNewMessages);
        void onFail(ErrorType errorType, String errorString, int errorCode);
    }


    public LoadMoreMessageLogic(String folder, LoadMoreCallback loadMoreCallback) {
        this.folder = folder;
        this.loadMoreCallback = loadMoreCallback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        int iteration = loadIterationIndex();
        iteration++;

        if (!loadMessages(iteration) )
            return false;

        saveIterationIndex(iteration);
        return true;
    }

    private boolean loadMessages(int iteration) {
        LoadMessageLogic loadMessageLogic = new LoadMessageLogic(folder);
        loadMessageLogic.setIteration(iteration);
        loadMessageLogic.setSaveToTempCache(true);
        LoadMessageLogic.LoadMessageAnswer answer = loadMessageLogic.load();
        if (answer.success) {
            haveNewValue = answer.haveNewValue;
            return true;
        }
        else {
            this.errorType = answer.errorType;
            this.errorCode = answer.errorCode;
            this.errorString = answer.errorString;
            return false;
        }
    }

    private static void saveIterationIndex(int index) {
        Context context = PrivateMailApplication.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("iteration", index);
        editor.apply();
    }

    private int loadIterationIndex() {
        Context context = PrivateMailApplication.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int index = sharedPreferences .getInt("iteration", 0);
        return index;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        if ( isCancelled()) return;

        if (result) {
            loadMoreCallback.onSuccess( haveNewValue );
        }
        else {
            loadMoreCallback.onFail(errorType, errorString, errorCode);
        }

    }


    public static void clearTemp() {
        saveIterationIndex(0);
        RemoveTempMessagesLogic logic = new RemoveTempMessagesLogic();
        logic.execute();
    }
}
