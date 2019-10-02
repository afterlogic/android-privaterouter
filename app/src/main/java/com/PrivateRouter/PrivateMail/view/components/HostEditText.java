package com.PrivateRouter.PrivateMail.view.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;

import org.jetbrains.annotations.NotNull;

public class HostEditText extends AppCompatEditText {
    float mOriginalLeftPadding = -1;
    private Paint prefixTextPaint = new Paint();
    private Paint textPaint = new Paint();
    private boolean handleChanges = true;
    private final String HTTP_PREFIX = "http://";
    private final String HTTPS_PREFIX = "https://";


    public HostEditText(Context context) {
        super(context);
        init();
    }

    public HostEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HostEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (1==1)
            return;

        textPaint = new Paint( getPaint() );
        textPaint.setColor(getContext().getResources().getColor(R.color.color_white));

        prefixTextPaint = new Paint( getPaint() );
        prefixTextPaint.setColor(getContext().getResources().getColor(R.color.color_dark_gray));


        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!handleChanges)
                    return;
                if (TextUtils.isEmpty((CharSequence) getTag())) {
                    String text = s.toString().toLowerCase();
                    if (text.startsWith(HTTP_PREFIX) )
                        setPrefix(HTTP_PREFIX);
                    else if (text.startsWith(HTTPS_PREFIX) )
                        setPrefix(HTTPS_PREFIX);
                }

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  //      calculatePrefix();
    }
/*
    private void calculatePrefix() {
        if (mOriginalLeftPadding == -1) {
            String prefix = (String) getTag();
            float[] widths = new float[prefix.length()];
            getPaint().getTextWidths(prefix, widths);
            float textWidth = 0;
            for (float w : widths) {
                textWidth += w;
            }
            mOriginalLeftPadding = getCompoundPaddingLeft();
            setPadding((int) (textWidth + mOriginalLeftPadding),
                    getPaddingRight(), getPaddingTop(),
                    getPaddingBottom());
        }
    }
*/
    //@Override
    public int getHorizontalOffsetForDrawables() {
        return 110;
    }
/*
    @Override
    protected void onDraw(Canvas canvas) {

        String prefix =   (String) getTag();
        canvas.drawText(prefix, getCompoundPaddingLeft(),
                getLineBounds(0, null), getPrefixPaint());


        canvas.drawText(getText().toString(), getCompoundPaddingLeft() + getPrefixWidth(),
                getLineBounds(0, null), getTextPaint());

    }
*/
    private Paint getTextPaint() {
        return textPaint;
    }

    private Paint getPrefixPaint() {
        return prefixTextPaint;
    }

    private float getPrefixWidth() {
        String prefix = (String) getTag();
        float[] widths = new float[prefix.length()];
        getPaint().getTextWidths(prefix, widths);
        float textWidth = 0;
        for (float w : widths) {
            textWidth += w;
        }
        return textWidth;
    }

    private void setPrefix(@NotNull String prefix) {
        handleChanges = false;

        Log.d("setPrefix", "setPrefix="+prefix);
        this.setTag(prefix);

        String text = getText().toString();
        text = text.substring(prefix.length());

        setText( text );


        handleChanges = true;
    }

    public String getFullText() {
        return (String)getTag() + getText();
    }
}