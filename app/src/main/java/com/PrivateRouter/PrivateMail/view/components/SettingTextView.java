package com.PrivateRouter.PrivateMail.view.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingTextView extends FrameLayout implements View.OnClickListener {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_value)
    TextView tvValue;

    String[] variants;
    private int selected;

    public OnSelectInterface getOnSelectInterface() {
        return onSelectInterface;
    }

    public void setOnSelectInterface(OnSelectInterface onSelectInterface) {
        this.onSelectInterface = onSelectInterface;
    }

    public interface OnSelectInterface {
        void onSelect(int position, String value);
    }

    OnSelectInterface onSelectInterface;

    public SettingTextView(Context context) {
        super(context);
        init(context, null);
    }


    public SettingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public  void setVariants(String[] variants)
    {
        this.variants = variants;
    }

    public void setSelectedVariant(int index)
    {
        if ( variants==null || index<0 || index>=variants.length  )
            return;

        selected = index;
        selectVariant(index);
    }

    private void selectVariant(int index) {
        tvValue.setText( variants[index]);
        if (onSelectInterface!=null)
            onSelectInterface.onSelect(index, variants[index]);
    }

    public int getSelectedVariant() {
        return selected;
    }




    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.setting_textview,null);
        addView(view);
        ButterKnife.bind(this);

        view.setOnClickListener( this);


        TypedArray theAttrs = context.obtainStyledAttributes(attrs, R.styleable.SettingTextView );
        String name  = theAttrs.getString(R.styleable.SettingTextView_android_name );
        tvName.setText(name);

        theAttrs.recycle();
    }

    @Override
    public void onClick(View view) {
        if (!(getContext() instanceof AppCompatActivity))
            return;

        AppCompatActivity activity = (AppCompatActivity) getContext();

        SettingsTextViewDialog dialogFragment = new SettingsTextViewDialog();
        dialogFragment.setVariants(variants);
        dialogFragment.setOnSelectInterface(new SettingsTextViewDialog.OnSelectInterface() {
            @Override
            public void onSelect(int index, String value) {
                setSelectedVariant(index);
            }
        });
        dialogFragment.show( activity.getSupportFragmentManager(), "settingsTextViewDialog ");
    }
}
