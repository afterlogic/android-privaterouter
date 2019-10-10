package com.PrivateRouter.PrivateMail.view.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Email;
import com.PrivateRouter.PrivateMail.model.EmailCollection;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.common.HtmlImageGetter;
import com.PrivateRouter.PrivateMail.view.common.HtmlTagHandler;
import com.PrivateRouter.PrivateMail.view.common.WebViewPrivateMailClient;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http2.Header;

public class MessageUtils {

    public static void setMessageBody(String message, WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient( new WebViewPrivateMailClient());

        webView.loadDataWithBaseURL("", message, "text/html", "UTF-8", "");

    }



    public static void setMessageBody(Message message, TextView textView) {
        if (!TextUtils.isEmpty(message.getPlain())) {
            String text = message.getPlain().trim();
            Logger.d("MessageUtils", "text");

            int index = text.indexOf("Comment");
            int index2 =  text.indexOf("<br />", index+1);
            if (index>=0 && index2>=0) {
                text = text.substring(0, index) + text.substring(index2);
            }
            text = text.replace("<br />", "\n");

            textView.setText(text);
        }
        else if (!TextUtils.isEmpty(message.getHtml()) ){
            String html = message.getHtml();
            html = html.replace("<img data-x-src-cid=", "<img src=");
            textView.setText(Html.fromHtml(html, new HtmlImageGetter(textView, message), null/*new HtmlTagHandler()*/) );
        }

    }

    public static boolean isEncrypted(Message message) {
        return !TextUtils.isEmpty(message.getPlain()) && message.getPlain().contains("-----BEGIN PGP MESSAGE-----")
                && message.getPlain().contains("-----END PGP MESSAGE-----");
    }

    public static boolean isSigned(Message message) {
        return !TextUtils.isEmpty(message.getPlain()) && message.getPlain().contains("-----BEGIN PGP SIGNATURE-----")
                && message.getPlain().contains("-----END PGP SIGNATURE-----");
    }

    public static String convertToPlain(String html) {
        return android.text.Html.fromHtml(html).toString();
    }




}
