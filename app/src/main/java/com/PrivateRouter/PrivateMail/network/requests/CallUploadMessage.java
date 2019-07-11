package com.PrivateRouter.PrivateMail.network.requests;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.network.responses.UploadAttachmentResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.utils.UriUtils;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallUploadMessage extends CallRequest<BaseResponse>  implements Callback<UploadAttachmentResponse> {

    Uri uri;
    public CallUploadMessage(CallRequestResult<BaseResponse> callback) {
        super(callback);
    }




    public void setFile(Uri uri) {
        this.uri = uri;

    }

    @Override
    public void start() {

        Parameters parameters = new Parameters();
        parameters.AccountID = LoggedUserRepository.getInstance().getActiveAccount().getAccountID();
        Gson gson = new Gson();

        String json = gson.toJson(parameters);


        Context context = PrivateMailApplication.getContext();

        String path = UriUtils.getPath(PrivateMailApplication.getContext(), uri);
        File file = new File(path);



        String fileData = getFileData();
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        //RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileData);


        //MultipartBody.Part body2 =  MultipartBody.Part.createFormData("file", "name", requestFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("jua-uploader", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));


        RequestBody module = RequestBody.create(MediaType.parse("text/plain"), ApiModules.MAIL);
        RequestBody method = RequestBody.create(MediaType.parse("text/plain"), ApiMethods.UPLOAD_ATTACHMENT);
        RequestBody parameter = RequestBody.create(MediaType.parse("text/plain"), json);
        RequestBody requestFile2 = RequestBody.create(MediaType.parse("text/plain"), fileData);


        Call<UploadAttachmentResponse> call= ApiFactory.getService().uploadAttachment( filePart, module,  method,
                 parameter   );
        call.enqueue(this);
    }

    private String getFileData() {

        StringBuilder text = new StringBuilder();

        try {
            Context context = PrivateMailApplication.getContext();
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String line = null;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {

                while ((line = bufferedReader.readLine()) != null) {
                    text.append(line + "\n");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


        return text.toString();
    }


    @Override
    public void onResponse(Call<UploadAttachmentResponse> call, Response<UploadAttachmentResponse> response) {

        if (response.isSuccessful() ) {
            UploadAttachmentResponse baseResponse = response.body();
            if (baseResponse !=null && baseResponse.isSuccess() && baseResponse.getResult()!=null ) {

                if (callback!=null)
                    callback.onSuccess(baseResponse  );
            }
            else {
                if (callback!=null)
                    callback.onFail(ErrorType.ERROR_REQUEST, baseResponse.getErrorCode() );
            }

        }
        else {
            if (callback!=null)
                callback.onFail(ErrorType.SERVER_ERROR, response.code() );
        }
    }

    @Override
    public void onFailure(Call<UploadAttachmentResponse> call, Throwable t) {
        if (callback!=null)
            callback.onFail(ErrorType.FAIL_CONNECT, 0);
    }

    class Parameters {
        int AccountID;
    }
}
