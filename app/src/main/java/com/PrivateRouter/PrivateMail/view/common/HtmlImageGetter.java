package com.PrivateRouter.PrivateMail.view.common;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HtmlImageGetter implements Html.ImageGetter {

    Message message;
    TextView container;

    public HtmlImageGetter(TextView container, Message message) {
        this.message = message;
        this.container = container;
    }



    @Override
    public Drawable getDrawable(String s) {
        if (message.getAttachments()!=null) {


             Attachments attachments = message.getAttachments().find(s);
             if (attachments!= null) {
                 String url =  ApiFactory.getUrl() + attachments.getActions().getDownload().getUrl();
                 try {
                     URLDrawable urlDrawable = new URLDrawable();
                     //ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);
                     //asyncTask.execute(url);
                     OkHttpClient client = new OkHttpClient.Builder()
                             .addInterceptor(new Interceptor() {
                                 @Override
                                 public Response intercept(Chain chain) throws IOException {
                                     Request newRequest = chain.request().newBuilder()
                                             .addHeader("Authorization", "Bearer "+ApiFactory.getToken())
                                             .build();
                                     return chain.proceed(newRequest);
                                 }
                             })
                             .build();


                     Picasso picasso = new Picasso.Builder(container.getContext())
                             .downloader(new OkHttp3Downloader(client))
                             .build();

                     picasso.load(url).into(new Target() {
                         @Override
                         public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                             urlDrawable.drawable =  new BitmapDrawable(container.getContext().getResources(), bitmap);// PrivateMailApplication.getContext().getDrawable(R.drawable.logo);
                             urlDrawable.drawable .setBounds(0, 0, urlDrawable.drawable.getIntrinsicWidth(), urlDrawable.drawable.getIntrinsicHeight());
                             //urlDrawable.drawable = new BitmapDrawable(bitmap);
                             //urlDrawable.drawable .setBounds(0, 0, urlDrawable.drawable.getIntrinsicWidth(), urlDrawable.drawable.getIntrinsicHeight());
                             HtmlImageGetter.this.container.invalidate();
                         }

                         @Override
                         public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            e.printStackTrace();
                         }

                         @Override
                         public void onPrepareLoad(Drawable placeHolderDrawable) {

                         }
                     });
                     return urlDrawable;



                     //Bitmap bitmap = Picasso.get().load(url).get();
                        //return new BitmapDrawable(bitmap);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }

             }
        }
        return null;
    }



    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result!=null) {
                urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight()); //set the correct bound according to the result from HTTP call
                urlDrawable.drawable = result;
                HtmlImageGetter.this.container.invalidate();
            }
        }

        public Drawable fetchDrawable(String urlString) {
            try {
                URL url = new URL(urlString);
                InputStream is = url.openConnection().getInputStream();
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight());
                return drawable;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
