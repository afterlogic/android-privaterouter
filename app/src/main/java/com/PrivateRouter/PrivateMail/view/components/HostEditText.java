package com.PrivateRouter.PrivateMail.view.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;

public class HostEditText extends AppCompatEditText {
    private boolean isNeedNoChangeSomeCharacters = true;
    private String charactersNoChange;
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
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isNeedNoChangeSomeCharacters) {

                    if (TextUtils.isEmpty(charactersNoChange)) {
                        String text = s.toString().toLowerCase();
                        if (text.startsWith(HTTP_PREFIX) )
                            setCharactersNoChangeInitial(HTTP_PREFIX);
                        else if (text.startsWith(HTTPS_PREFIX) )
                            setCharactersNoChangeInitial(HTTPS_PREFIX);
                    }
                    else if (charactersNoChange != null) {
                        if (!getText().toString().startsWith(charactersNoChange)) {
                            removeTextChangedListener(this);
                            if (charactersNoChange.length() > s.length()) {
                                setText(charactersNoChange);
                            } else {
                                setText(charactersNoChange + getText());
                            }
                            setSelection(getText().toString().length());
                            addTextChangedListener(this);
                        }
                        else {
                            removeTextChangedListener(this);
                            setText(getText());
                            setSelection(getText().toString().length());
                            addTextChangedListener(this);
                        }
                    }
                }

            }
        });
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (isNeedNoChangeSomeCharacters && charactersNoChange != null) {
            if (length() > charactersNoChange.length() && selStart < charactersNoChange.length()) {
                setSelection(charactersNoChange.length(),selEnd);
            }
        }
    }


    @Override
    public void setText(CharSequence text, BufferType type) {

        if (isNeedNoChangeSomeCharacters) {

            if (TextUtils.isEmpty(charactersNoChange)) {
                super.setText(text, type);
                String textToCheck = text.toString();
                if (textToCheck.startsWith(HTTP_PREFIX)) {
                    setCharactersNoChangeInitial(HTTP_PREFIX);
                    setText(text, type);
                }
                else if (textToCheck.startsWith(HTTPS_PREFIX)) {
                    setCharactersNoChangeInitial(HTTPS_PREFIX);
                    setText(text, type);
                }

            }
            else {

                Context context = getContext();
                int colorBlockedColor = context.getResources().getColor(R.color.color_dark_gray);
                int colorText = getCurrentTextColor();

                Spannable spannable = new SpannableString(text);
                spannable.setSpan(new ForegroundColorSpan(colorText), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                if (charactersNoChange != null)
                    spannable.setSpan(new ForegroundColorSpan(colorBlockedColor), 0, charactersNoChange.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                super.setText(spannable, type);
                if (isNeedNoChangeSomeCharacters && charactersNoChange != null) {
                    if (!getText().toString().trim().startsWith(charactersNoChange)) {
                        setText(charactersNoChange + getText());
                    }
                }
            }
        }
        else {
            super.setText(text, type);
        }
    }

    public void setCharactersNoChangeInitial(String charactersNoChange) {
        isNeedNoChangeSomeCharacters = true;
        this.charactersNoChange = charactersNoChange;

        if (!getText().toString().trim().startsWith(charactersNoChange)) {
            setText(getText());
        }
    }



}

