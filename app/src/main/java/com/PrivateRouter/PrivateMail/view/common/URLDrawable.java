package com.PrivateRouter.PrivateMail.view.common;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;

public class URLDrawable extends BitmapDrawable {
    protected Drawable drawable;

    @Override
    public void draw(Canvas canvas) {
        if (drawable==null) {
            //drawable = PrivateMailApplication.getContext().getDrawable(R.drawable.logo);
            //drawable .setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        if(drawable != null) {
            drawable.draw(canvas);
        }
    }
}