package com.PrivateRouter.PrivateMail.view.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.view.utils.Logger;

import org.jetbrains.annotations.NotNull;

public class HostEditText extends AppCompatEditText {
    float mOriginalLeftPadding = -1;
    private Paint prefixTextPaint = new Paint();
    private Paint textPaint = new Paint();
    private boolean handleChanges = true;
    private final String HTTP_PREFIX = "http://";
    private final String HTTPS_PREFIX = "https://";
    private int shiftX;
    private boolean disabled = true;


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
        if (disabled)
            return;

        textPaint = new Paint( getPaint() );
        textPaint.setColor(getContext().getResources().getColor(R.color.color_white));

        prefixTextPaint = new Paint( getPaint() );
        prefixTextPaint.setColor(getContext().getResources().getColor(R.color.color_dark_gray));

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && getText().toString().isEmpty()) {
                    clearPrefix();
                }
            }
        });

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
        shiftX = (int) getPrefixWidth();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

         shiftX = 0;

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
    @Override
    public int getCompoundPaddingLeft() {
        return shiftX;
    }
    @Override
    public int getPaddingStart() {
        return shiftX;
    }
    @Override
    protected void onDraw(Canvas canvas) {

        String prefix =   (String) getTag();
        canvas.drawText(prefix, 0,
                getLineBounds(0, null), getPrefixPaint());


      //  updateLeftPadding();

        shiftX = (int) getPrefixWidth();

        super.onDraw(canvas);

    //    shiftX = 0;

        //setPadding(0 ,getPaddingRight(), getPaddingTop(), getPaddingBottom());
        //canvas.drawText(getText().toString(), getCompoundPaddingLeft() + getPrefixWidth(),
          //      getLineBounds(0, null), getTextPaint());

    }

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

    private void clearPrefix() {
        setTag("");
        //updateLeftPadding();
    }

    private void setPrefix(@NotNull String prefix) {
        handleChanges = false;

        Logger.d("setPrefix", "setPrefix="+prefix);
        this.setTag(prefix);

        String text = getText().toString();
        text = text.substring(prefix.length());

        setText( text );

        //updateLeftPadding();

        handleChanges = true;
    }

    private void updateLeftPadding() {
        float textWidth = getPrefixWidth();
        //mOriginalLeftPadding = getCompoundPaddingLeft();
        setPadding((int) (textWidth ),
                getPaddingRight(), getPaddingTop(),
                getPaddingBottom());

    }

    public String getFullText() {
        return (String)getTag() + getText();
    }
}