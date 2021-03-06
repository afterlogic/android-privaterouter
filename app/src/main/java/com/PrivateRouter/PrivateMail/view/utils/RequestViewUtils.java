package com.PrivateRouter.PrivateMail.view.utils;

import android.app.ProgressDialog;
import android.content.Context;
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
        if (progressDialog!=null)
            progressDialog.setMessage(string);
    }


    public static void hideRequest() {
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    public static void showError(Context context, ErrorType errorType, int serverError) {

        String[] strings = context.getResources().getStringArray(R.array.request_server_error);
        String str = strings[errorType.ordinal()];
        if (serverError>0) {
            str = str + ". "+ context.getString(R.string.request_error_code)+": "+ serverError;
        }

        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

    }
}
