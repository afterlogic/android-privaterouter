package com.PrivateRouter.PrivateMail.network.logics;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.PrivateRouter.PrivateMail.network.AfterLogicAPI;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.responses.GetMessageBaseResponse;
import com.PrivateRouter.PrivateMail.view.utils.Logger;
import com.PrivateRouter.PrivateMail.view.utils.PathUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadLogic extends AsyncTask<Void, Void, Boolean> {
    private static  final String TAG = "DownloadLogic";
    private final String name;
    private String fileUrl;
    private File file;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isWriteToFile() {
        return writeToFile;
    }

    public void setWriteToFile(boolean writeToFile) {
        this.writeToFile = writeToFile;
    }

    public interface OnDownloadCallback {
    void onDownload (boolean success);
    }
    private OnDownloadCallback  onDownloadCallback;
    private boolean writeToFile = true;
    private String data = "";

    public DownloadLogic( @NonNull Context context, @NonNull String url, @NonNull String name, OnDownloadCallback  onDownloadCallback) {

        this.name = name;
        fileUrl = url;
        this.onDownloadCallback = onDownloadCallback;


    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        file = PathUtils.getUniqueFile(name);

        boolean success = false;
        try {
            Call<ResponseBody> call = ApiFactory.getService().downloadAttachment(fileUrl);
            Response<ResponseBody> response =  call.execute();
            if (response!=null) {
                if (writeToFile)
                    success = writeResponseBodyToDisk(response.body());
                else
                    success = writeResponseBodyToString(response.body());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }


    private boolean writeResponseBodyToString(ResponseBody body) {
        try {
            InputStream inputStream = body.byteStream();
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line+"\n");
                }
            }

            data = stringBuilder.toString();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {

        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();

                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Logger.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (onDownloadCallback!=null)
            onDownloadCallback.onDownload(result);
    }

}
