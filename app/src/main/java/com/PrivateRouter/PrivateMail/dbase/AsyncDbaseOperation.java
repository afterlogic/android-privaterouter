package com.PrivateRouter.PrivateMail.dbase;

import android.os.AsyncTask;

public class AsyncDbaseOperation extends AsyncTask <Void, Void, Void> {

    Runnable runnableOperation;
    Runnable onFinishCallback;

    @Override
    protected Void doInBackground(Void... voids) {
        if(runnableOperation != null){
            runnableOperation.run();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if(onFinishCallback != null){
            onFinishCallback.run();
        }
    }

    public Runnable getRunnableOperation() {
        return runnableOperation;
    }

    public void setRunnableOperation(Runnable runnableOperation) {
        this.runnableOperation = runnableOperation;
    }

    public Runnable getOnFinishCallback() {
        return onFinishCallback;
    }

    public void setOnFinishCallback(Runnable onFinishCallback) {
        this.onFinishCallback = onFinishCallback;
    }
}
