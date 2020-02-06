package com.PrivateRouter.PrivateMail.view.contacts;

import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Storages;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StorageViewHolder  extends RecyclerView.ViewHolder {


    @BindView(R.id.tv_storage)
    TextView tvStorage;

    private int position;
    private Storages storages;


    private StorageAdapter.OnStorageClick onStorageClick;
    private ColorStateList oldColors;


    public StorageViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.tv_storage)
    public void onClick() {
        onStorageClick.onStorageClick(storages.getId());
    }


    public void bind(Storages storages, int position, boolean selected) {
        this.storages = storages;
        this.position = position;

        tvStorage.setText(storages.getId());
        if (oldColors == null)
            oldColors = tvStorage.getTextColors();

        if (selected ) {
            tvStorage.setBackgroundColor(tvStorage.getContext().getResources().getColor(R.color.colorPrimary) );
            tvStorage.setTextColor(tvStorage.getContext().getResources().getColor(R.color.color_white));
        }
        else  {
            tvStorage.setTextColor(oldColors);
            tvStorage.setBackgroundColor(tvStorage.getContext().getResources().getColor(android.R.color.transparent) );
        }
    }


    public StorageAdapter.OnStorageClick getOnStorageClick() {
        return onStorageClick;
    }

    public void setOnStorageClick(StorageAdapter.OnStorageClick onStorageClick) {
        this.onStorageClick = onStorageClick;
    }
}
