package com.PrivateRouter.PrivateMail.network;

import android.support.annotation.NonNull;

import com.PrivateRouter.PrivateMail.BuildConfig;
import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.PrivateRouter.PrivateMail.network.responses.GetFoldersMetaResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiFactory {

    private static OkHttpClient sClient;

    private static AfterLogicAPI sService;

    public static final String TAG = "API";

    private static String token = "";

    private static boolean test = true;

    @NonNull
    public static AfterLogicAPI getService() {
        AfterLogicAPI service = sService;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = sService;
                if (service == null) {
                    service = sService = createService();
                }
            }
        }
        return service;
    }

    public static void useTest() {
        test = true;
        synchronized (ApiFactory.class) {
            sService = null;
        }
    }

    private static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(MessageBase.class, new MessageBaseDeserializer() ).
                registerTypeAdapter(GetFoldersMetaResponse.class, new GetFolderMetaDeserializer() ).
                registerTypeAdapter(Attachments.class, new AttachmentsDeserializer() ).
                create();
    }

    @NonNull
    private static AfterLogicAPI createService() {
        String url = getUrl();

        return new Retrofit.Builder()
                .baseUrl(url )
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(AfterLogicAPI.class);

    }

    public static String getUrl() {
        if (test)
            return BuildConfig.API_ENDPOINT_TEST;
        else
            return BuildConfig.API_ENDPOINT;
    }

    @NonNull
    private static OkHttpClient getClient() {
        OkHttpClient client = sClient;
        if (client == null) {
            synchronized (ApiFactory.class) {
                client = sClient;
                if (client == null) {
                    client = sClient = buildClient();
                }
            }
        }
        return client;
    }



    @NonNull
    private static OkHttpClient buildClient() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.readTimeout(60, TimeUnit.SECONDS);
        clientBuilder.connectTimeout(60, TimeUnit.SECONDS);

        Interceptor headerAuthorizationInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request request = chain.request();

                Headers.Builder headersBuilder =  request.headers().newBuilder();
                if (token!=null) {
                    headersBuilder.add("Authorization", "Bearer "+token);
                }
               // headersBuilder.add("Content-Type", "application/x-www-form-urlencoded");
                Headers headers = headersBuilder.build();

                request = request.newBuilder().headers(headers).build();
                return chain.proceed(request);
            }
        };
        clientBuilder.addInterceptor(headerAuthorizationInterceptor);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(interceptor);

        return clientBuilder.build();
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        ApiFactory.token = token;
    }
}
