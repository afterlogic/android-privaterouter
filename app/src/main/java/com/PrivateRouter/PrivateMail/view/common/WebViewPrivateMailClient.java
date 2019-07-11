package com.PrivateRouter.PrivateMail.view.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http2.Header;

public class WebViewPrivateMailClient extends WebViewClient {

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        String url = request.getUrl().toString();

        return getNewResponse(url);
    }

    private WebResourceResponse getNewResponse(String url) {

        try {
            OkHttpClient httpClient = new OkHttpClient();

            String authToken = "Bearer "+ApiFactory.getToken();
            Request request = new Request.Builder()
                    .url(url.trim())
                    .addHeader("Authorization", authToken)
                    .build();

            Response response = httpClient.newCall(request).execute();

            return new WebResourceResponse(
                    null,
                    response.header("content-encoding", "utf-8"),
                    response.body().byteStream()
            );

        } catch (Exception e) {
            return null;
        }

    }
/*
    @SuppressWarnings("deprecation")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        final String mime = URLConnection.guessContentTypeFromName(url);
        boolean loadImg = url.contains( ApiFactory.getUrl() );
        if (!loadImg) {
            return super.shouldInterceptRequest(view, url);
        }

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            String authToken = "Bearer "+ApiFactory.getToken();
                            Log.e("WebView", "authToken ="+authToken );
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", authToken)
                                    .build();
                            return chain.proceed(newRequest);
                        }
                    })
                    .build();


            Context context = PrivateMailApplication.getContext();
            Picasso picasso = new Picasso.Builder( context ).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            })
                    .downloader(new OkHttp3Downloader(client))
                    .build();

            final Bitmap image = picasso.load(url).get();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);

            InputStream in = new ByteArrayInputStream(out.toByteArray());
            return new WebResourceResponse(mime, "UTF-8", in);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Unable to load image", e);
            return super.shouldInterceptRequest(view, url);
        }
    }
*/

}
