package com.PrivateRouter.PrivateMail.view.contacts;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Storages;

import java.util.List;

public class StorageAdapter extends RecyclerView.Adapter<StorageViewHolder> {

    private final String selectStorage;
    private OnStorageClick onStorageClick;
    private List<Storages> storages;

    StorageAdapter(List<Storages> storages, String currentStorage){
        this.storages = storages;
        this.selectStorage = currentStorage;
    }

    @NonNull
    @Override
    public StorageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_storage, viewGroup, false);
        StorageViewHolder storageViewHolder = new StorageViewHolder(view);
        storageViewHolder.setOnStorageClick(onStorageClick);
        return storageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StorageViewHolder groupsViewHolder, int i) {
        Storages storage = storages.get(i);
        boolean selected = !TextUtils.isEmpty(selectStorage ) && (storage.getId().equals(selectStorage));
        groupsViewHolder.bind(storage, i, selected );
    }

    @Override
    public int getItemCount() {
        return storages.size();
    }

    public OnStorageClick getOnStorageClick() {
        return onStorageClick;
    }

    public void setOnStorageClick(OnStorageClick onStorageClick) {
        this.onStorageClick = onStorageClick;
    }


    public interface OnStorageClick{
        void onStorageClick(String storage);
    }


}
