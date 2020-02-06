package com.PrivateRouter.PrivateMail.view.folders_list;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Folder;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderViewHolder> {
    private List<Folder> folderList;
    private LayoutInflater layoutInflater;
    private String currentFolder;

    public OnFolderClick getOnFolderClick() {
        return onFolderClick;
    }

    public void setOnFolderClick(OnFolderClick onFolderClick) {
        this.onFolderClick = onFolderClick;
    }


    public interface OnFolderClick{
        void onFolderClick(Folder folder);
        void onFolderUnreadClick(Folder folder);
    }

    private OnFolderClick onFolderClick;

    public FolderAdapter(Context context, List<Folder> folderList, String currentFolder){
        this.folderList = folderList;
        this.currentFolder = currentFolder;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setFolderList(List<Folder> folderList) {
        this.folderList = folderList;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_folders_list, viewGroup, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder folderViewHolder, int i) {
        Folder folder = folderList.get(i);
        folderViewHolder.bind(folder, currentFolder);
        folderViewHolder.setOnFolderClick(onFolderClick);
    }

    @Override
    public int getItemCount() {
        if (folderList == null){
            return 0;
        } else
            return folderList.size();
    }


}
