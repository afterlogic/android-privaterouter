package com.PrivateRouter.PrivateMail.view.mail_view;

import android.Manifest;
import android.arch.persistence.room.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.ImportTask;
import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.logics.DownloadLogic;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.common.FragmentWithRequestPermission;
import com.PrivateRouter.PrivateMail.view.settings.PGPKeyActivity;
import com.PrivateRouter.PrivateMail.view.settings.PGPSettingsActivity;
import com.PrivateRouter.PrivateMail.view.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AttachmentsViewHolder extends RecyclerView.ViewHolder{
    Attachments attachments;

    @BindView(R.id.tv_attachment_name)
    TextView attachmentName;

    @BindView(R.id.tv_attachment_size)
    TextView attachmentSize;

    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    @BindView(R.id.iv_attachment_download)
    ImageView ivAttachmentDownload;

    @BindView(R.id.iv_attachment_remove)
    ImageView ivAttachmentRemove;


    @BindView(R.id.iv_open_pgp_key)
    ImageView ivOpenPgpKey;


    AttachmentsAdapter attachmentsAdapter;
    private AttachmentsAdapter.OnAttachmentClick onAttachmentClick;

    private DownloadLogic downloadLogic;

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_open_pgp_key)
    public void bgOpenPgpKeyClick() {
        Context context = attachmentName.getContext();
        if (attachments.getActions()!=null && attachments.getActions()!=null) {
            Attachments.Actions.UrlWrapper urlWrapper = attachments.getActions().getDownload();

            downloadLogic = new DownloadLogic(context, ApiFactory.getUrl() + urlWrapper.getUrl(),
                    attachments.getFileName(), this::onPgpOpen);
            downloadLogic.setWriteToFile(false);
            downloadLogic.execute();
            showProgressBar();
        }
    }

    private void onPgpOpen(boolean b) {
        if (downloadLogic!=null && b) {
            String fileData = downloadLogic.getData();
            downloadLogic = null;

            Context context = PrivateMailApplication.getContext();
            Intent intent = new Intent(context, PGPSettingsActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(PGPSettingsActivity.IMPORT_KEY_PARAMETER, fileData);

            PrivateMailApplication.getInstance().startActivity(intent);
        }
        hideProgressBar();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_attachment_download)
    public void bgDownloadClick() {

        FragmentWithRequestPermission fragment = null;
        if (onAttachmentClick!=null)
            fragment = onAttachmentClick.onDownloadAttachmentClick(attachments);

        Context context = attachmentName.getContext();
        if (attachments.getActions()!=null && attachments.getActions()!=null) {
            Attachments.Actions.UrlWrapper urlWrapper = attachments.getActions().getDownload();


            boolean havePermission = fragment.checkAndRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, this::bgDownloadClick);


            if (havePermission) {

                DownloadLogic downloadLogic = new DownloadLogic(context, ApiFactory.getUrl() + urlWrapper.getUrl(), attachments.getFileName(),
                        this::onDownload);
                downloadLogic.execute();
                showProgressBar();
            }
        }

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_attachment_remove)
    public void bgRemoveClick() {
        attachmentsAdapter.removeAttachment(attachments);
    }

    private void showProgressBar() {
        pbLoading.setVisibility(View.VISIBLE);
        ivAttachmentDownload.setVisibility(View.INVISIBLE);
        ivOpenPgpKey.setVisibility(View.INVISIBLE);
    }

    private void onDownload(boolean success) {
        Context context = attachmentName.getContext();
        if (success) {
            Toast.makeText(context, context.getString(R.string.attachment_download_loaded), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, context.getString(R.string.attachment_download_failed), Toast.LENGTH_SHORT).show();
        }
        hideProgressBar();
    }


    private void hideProgressBar() {
        pbLoading.setVisibility(View.INVISIBLE);
        ivAttachmentDownload.setVisibility(View.VISIBLE);
        if (isKeyFile() )
            ivOpenPgpKey.setVisibility(View.VISIBLE);
    }

    public AttachmentsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    public void bind(Attachments attachments, boolean removeMode, AttachmentsAdapter attachmentsAdapter) {
        this.attachments = attachments;
        this.attachmentsAdapter = attachmentsAdapter;

        attachmentSize.setText(Utils.getSizeText(itemView.getContext(), attachments.getEstimatedSize()) );

        String fileName = attachments.getFileName();
        attachmentName.setText(fileName);


        if (isKeyFile())
            ivOpenPgpKey.setVisibility(View.VISIBLE);
        else
            ivOpenPgpKey.setVisibility(View.GONE);

        if (removeMode) {
            ivAttachmentDownload.setVisibility(View.INVISIBLE);
            ivAttachmentRemove.setVisibility(View.VISIBLE);
        }
        else {
            ivAttachmentDownload.setVisibility(View.VISIBLE);
            ivAttachmentRemove.setVisibility(View.INVISIBLE);
        }

        Context context = ivAttachmentDownload.getContext();
        if (SettingsRepository.getInstance().isNightMode(context)) {
            DrawableCompat.setTint(ivAttachmentDownload.getBackground(), ContextCompat.getColor(context.getApplicationContext(), R.color.color_white));
            ivAttachmentRemove.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cross_circled_white_16dp) );
        }
        else {
            DrawableCompat.setTint(ivAttachmentDownload.getBackground(), ContextCompat.getColor(context.getApplicationContext(), R.color.color_black));
            ivAttachmentRemove.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cross_circled_transp_16dp) );
        }
    }

    private boolean isKeyFile() {
        String fileName = attachments.getFileName();
        String ext = fileName.substring(fileName.length()-3 );
        return "asc".equalsIgnoreCase(ext);
    }

    public void setOnDownloadClick(AttachmentsAdapter.OnAttachmentClick onAttachmentClick) {
        this.onAttachmentClick = onAttachmentClick;
    }
}