package com.PrivateRouter.PrivateMail.view.contacts;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Storages;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StorageViewHolder  extends RecyclerView.ViewHolder {


    private int position;
    private Storages storages;


    private StorageAdapter.OnStorageClick onStorageClick;


    public StorageViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.item_storage)
    public void onClick() {
        onStorageClick.onStorageClick(storages.getId());
    }


    public void bind(Storages storages, int position) {
        this.storages = storages;
        this.position = position;
    }


    public StorageAdapter.OnStorageClick getOnStorageClick() {
        return onStorageClick;
    }

    public void setOnStorageClick(StorageAdapter.OnStorageClick onStorageClick) {
        this.onStorageClick = onStorageClick;
    }
}
