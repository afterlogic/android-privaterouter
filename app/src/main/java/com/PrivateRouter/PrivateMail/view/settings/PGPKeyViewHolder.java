package com.PrivateRouter.PrivateMail.view.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.PGPKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PGPKeyViewHolder extends RecyclerView.ViewHolder  {

    @BindView(R.id.cl_background)
    View cl_background;

    @BindView(R.id.tv_key_name)
    TextView tvKeyName;

    @BindView(R.id.tv_key_size)
    TextView tvKeySize;

    @BindView(R.id.cb_select)
    CheckBox cbSelect;

    public OnChecked getOnChecked() {
        return onChecked;
    }

    public void setOnChecked(OnChecked onChecked) {
        this.onChecked = onChecked;
    }

    public interface OnChecked {
        void onChecked(PGPKey pgpKey, boolean checked);
    }

    private OnChecked onChecked;

    @SuppressWarnings("unused")
    @OnClick(R.id.cl_background)
    public void bgViewClick() {
        cbSelect.setChecked( !cbSelect.isChecked() );
    }


    public PGPKeyViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    public void bind( PGPKey pgpKey) {
        Context context = tvKeyName.getContext();

        tvKeyName.setText( pgpKey.getUserID() );

        String str = String.format(context.getString(R.string.import_dialog_strength), pgpKey.getStrength(), pgpKey.getType());
        tvKeySize.setText(str);


        cbSelect.setOnCheckedChangeListener((compoundButton, b) -> {
            if (onChecked!=null)
                onChecked.onChecked(pgpKey, b);
        });


        PGPKey pubKey = PrivateMailApplication.getInstance().getKeysRepository().getKey(pgpKey.getUserID(),  pgpKey.getType());
        boolean disable =  pubKey!=null;
        cl_background.setEnabled(!disable);
        cbSelect.setEnabled(!disable);

        if (disable) {
            tvKeyName.setTextColor(context.getResources().getColor(R.color.color_dark_gray ));
            tvKeySize.setTextColor(context.getResources().getColor(R.color.color_dark_gray));
        }
        else {
            tvKeyName.setTextColor(context.getResources().getColor(R.color.color_black));
            tvKeySize.setTextColor(context.getResources().getColor(R.color.color_black));
        }
    }


}
