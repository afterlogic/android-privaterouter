package com.PrivateRouter.PrivateMail.encryption;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.network.logics.LoadMessagePoolLogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ImportFileTask  extends AsyncTask<Void, Void, ArrayList<PGPKey>> {
    public interface OnImportFile{
        void onImportFile(ArrayList<PGPKey> data);
    }
    private OnImportFile runnableOnFinish;
    private Uri uri;

    public ImportFileTask(Uri uri){
        this.uri = uri;
    }
    @Override
    protected ArrayList<PGPKey> doInBackground(Void... voids) {
        ArrayList<PGPKey> arrayList = null;

        StringBuilder text = new StringBuilder();
        try
        {

            Context context = PrivateMailApplication.getContext();

            String line = null;
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                while ((line = bufferedReader.readLine()) != null) {
                    text.append(line+"\n");
                }
            }



            arrayList = new ImportTask().importKeys(text.toString());
        }
        catch (IOException e)
        {
            Log.e("ImportFileTask", "File write failed: " + e.toString());
        }

        if (arrayList==null)
            arrayList = new ArrayList<>();

        return arrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<PGPKey> result) {
        if (runnableOnFinish!=null)
            runnableOnFinish.onImportFile(result);
    }

    public OnImportFile getRunnableOnFinish() {
        return runnableOnFinish;
    }

    public void setRunnableOnFinish(OnImportFile runnableOnFinish) {
        this.runnableOnFinish = runnableOnFinish;
    }
}
