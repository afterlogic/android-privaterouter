package com.PrivateRouter.PrivateMail.view.common;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.FolderType;

public class FolderDrawable {

    public static int getFolderIconResource(Context context, FolderType folderType){
        switch (folderType){
            case Inbox: return R.drawable.ic_folder_inbox;
            case Sent: return R.drawable.ic_folder_sent;
            case Spam:return R.drawable.ic_folder_sent;
            case Trash:return R.drawable.ic_folder_trash;
            case Drafts:return R.drawable.ic_folder_draft;
            case VirtualStarred: return R.drawable.ic_folder_starred;
        }
        return 0;
    }


    public static Drawable getFolderIcon(Context context, FolderType folderType){
        int resId = getFolderIconResource(context, folderType);
        if (resId>0)
            return context.getResources().getDrawable(resId);
        else
            return null;
    }
}
