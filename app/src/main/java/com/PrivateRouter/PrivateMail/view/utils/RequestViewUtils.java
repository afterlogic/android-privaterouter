package com.PrivateRouter.PrivateMail.view.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.R;

public class RequestViewUtils {
    private static ProgressDialog progressDialog;

    public static void showRequest(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.request_connection));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void setMessage(String string) {
        if (progressDialog != null)
            progressDialog.setMessage(string);
    }


    public static void hideRequest() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public static void showError(Context context, ErrorType errorType, String errorString, int serverError) {
        String str = "";
        if (errorType == ErrorType.ERROR_REQUEST && errorString != null) {
            str = errorString;
        } else {
            try {
                String[] strings = context.getResources().getStringArray(R.array.request_server_error);
                str = strings[errorType.ordinal()];
                if (serverError > 0) {
                    str = str + ". " + context.getString(R.string.request_error_code) + ": " + serverError;
                }

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        String finalStr = str;
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, finalStr, Toast.LENGTH_SHORT).show());

    }
}
