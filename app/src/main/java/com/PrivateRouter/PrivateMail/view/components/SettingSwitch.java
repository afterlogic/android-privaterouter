package com.PrivateRouter.PrivateMail.view.components;

import android.content.Context;
import android.content.res.TypedArray;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingSwitch  extends FrameLayout implements View.OnClickListener {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.sw_value)
    Switch swValue;



    public SettingSwitch(Context context) {
        super(context);
        init(context, null);
    }

    public void setOnValueChangeCallback(OnValueChangeCallback onValueChangeCallback) {
        this.onValueChangeCallback = onValueChangeCallback;
    }

    public void setCheck(boolean value) {
        swValue.setChecked(value);
    }


    public interface OnValueChangeCallback {
        void onValueChangeCallback(boolean value);
    }
    private OnValueChangeCallback onValueChangeCallback;

    public SettingSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.setting_switch,null);
        addView(view);
        ButterKnife.bind(this);

        view.setOnClickListener( this);

        swValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onValueChangeCallback!=null)
                    onValueChangeCallback.onValueChangeCallback(isChecked);
            }
        });

        TypedArray theAttrs = context.obtainStyledAttributes(attrs, R.styleable.SettingSwitch );
        String name  = theAttrs.getString(R.styleable.SettingSwitch_android_name );
        tvName.setText(name);

        theAttrs.recycle();
    }

    @Override
    public void onClick(View view) {


    }
}
