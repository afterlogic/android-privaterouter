package com.PrivateRouter.PrivateMail.view.mail_view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.view.common.FragmentWithRequestPermission;

import java.util.List;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsViewHolder> {
    private List<Attachments> attachmentsList;
    private LayoutInflater layoutInflater;
    boolean removeMode;
    private OnRemoveCallback onRemoveCallback;

    public OnRemoveCallback getOnRemoveCallback() {
        return onRemoveCallback;
    }

    public void setOnRemoveCallback(OnRemoveCallback onRemoveCallback) {
        this.onRemoveCallback = onRemoveCallback;
    }

    public interface OnRemoveCallback {
        void onRemove(Attachments attachments);
    }

    public AttachmentsAdapter(Context context, List<Attachments> attachmentsList, boolean removeMode ){
        this.attachmentsList = attachmentsList;
        this.removeMode = removeMode;

        layoutInflater = LayoutInflater.from(context);
    }

    public OnAttachmentClick getOnAttachmentClick() {
        return onAttachmentClick;
    }

    public void setOnAttachmentClick(OnAttachmentClick onAttachmentClick) {
        this.onAttachmentClick = onAttachmentClick;
    }

    public void removeAttachment(Attachments attachments) {
        attachmentsList.remove(attachments);
        if (onRemoveCallback!=null)
            onRemoveCallback.onRemove(attachments);
        notifyDataSetChanged();
    }

    public interface OnAttachmentClick{
        FragmentWithRequestPermission onDownloadAttachmentClick(Attachments attachments);
    }

    private OnAttachmentClick onAttachmentClick;


    @NonNull
    @Override
    public AttachmentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_mail_attachments, viewGroup, false);
        AttachmentsViewHolder  attachmentsViewHolder = new AttachmentsViewHolder(view);
        attachmentsViewHolder.setOnDownloadClick(onAttachmentClick);

        return attachmentsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentsViewHolder attachmentsViewHolder, int i) {
        Attachments attachments = attachmentsList.get(i);
        attachmentsViewHolder.bind(attachments, removeMode, this);
    }

    @Override
    public int getItemCount() {
        if (attachmentsList == null){
            return 0;
        } else
        return attachmentsList.size();
    }


}
