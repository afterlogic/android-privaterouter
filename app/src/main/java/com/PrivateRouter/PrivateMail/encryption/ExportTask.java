package com.PrivateRouter.PrivateMail.encryption;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.view.utils.Logger;
import com.PrivateRouter.PrivateMail.view.utils.PathUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;


public class ExportTask extends AsyncTask<String, Void, Void> {
    String fileName;

    public ExportTask(String fileName) {
        this.fileName = fileName;
    }

    private Runnable runnableOnFinish;

    @Override
    protected Void doInBackground(String... strings) {

        String data = "";
        for (String keys: strings) {
            data = data +keys+ "\n";
        }

        writeToFile(data);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (runnableOnFinish!=null)
            runnableOnFinish.run();
    }


    public void writeToFile(String data)
    {
        File file = PathUtils.getUniqueFile(fileName);

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Logger.e("Exception", "File write failed: " + e.toString());
        }
    }

    public Runnable getRunnableOnFinish() {
        return runnableOnFinish;
    }

    public void setRunnableOnFinish(Runnable runnableOnFinish) {
        this.runnableOnFinish = runnableOnFinish;
    }
}
