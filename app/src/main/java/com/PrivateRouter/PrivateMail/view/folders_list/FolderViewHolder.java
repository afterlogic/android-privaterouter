package com.PrivateRouter.PrivateMail.view.folders_list;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Folder;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.common.FolderDrawable;
import com.PrivateRouter.PrivateMail.view.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FolderViewHolder  extends RecyclerView.ViewHolder {

    @BindView(R.id.ll_background)
    View llBackground;

    @BindView(R.id.iv_icon)
    ImageView ivIcon;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_counter)
    TextView tvCounter;

    ColorStateList oldColors;

    Folder folder;
    private FolderAdapter.OnFolderClick onFolderClick;

    @SuppressWarnings("unused")
    @OnClick(R.id.ll_background)
    public void bgViewClick() {
        if (onFolderClick!=null)
            onFolderClick.onFolderClick(folder);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.tv_counter)
    public void tvCounterClick() {
        if (onFolderClick!=null)
            onFolderClick.onFolderUnreadClick(folder);
    }


    public FolderViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    public void bind(Folder folder, String currentFolder) {
        Context context = tvCounter.getContext();
        this.folder = folder;

        tvTitle.setText(folder.getName());
        if (!folder.isSelectable())
            llBackground.setOnClickListener(null);
        else
            llBackground.setOnClickListener(view -> bgViewClick());

        if (oldColors == null)
            oldColors = tvTitle.getTextColors();


        if (folder.getFullName().toLowerCase().equals( currentFolder.toLowerCase() )) {
            llBackground.setBackgroundColor( context.getResources().getColor(R.color.colorPrimary));
            tvTitle.setTextColor( context.getResources().getColor(R.color.color_white));
            ivIcon.setColorFilter(context.getResources().getColor(R.color.color_white));
        }
        else {
            tvTitle.setTextColor( oldColors );
            if (SettingsRepository.getInstance().isNightMode(context) )
                ivIcon.setColorFilter(context.getResources().getColor(R.color.color_white));
            else
                ivIcon.setColorFilter(context.getResources().getColor(R.color.color_black));


            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            llBackground.setBackgroundResource(outValue.resourceId);
        }

        if (folder.getMeta()!=null) {
            tvCounter.setVisibility(View.VISIBLE);
            if (folder.getType() == FolderType.Drafts.getId())
                tvCounter.setText(String.valueOf(folder.getMeta().getCount()) );
            else if (folder.getMeta().getUnreadCount()>0) {
                tvCounter.setText(String.valueOf(folder.getMeta().getUnreadCount()) );
            }
            else {
                tvCounter.setVisibility(View.INVISIBLE);
            }
        }
        else {
            tvCounter.setVisibility(View.INVISIBLE);
        }

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
        layoutParams.setMargins(Utils.getDP(context, 32* folder.getLevel()),0,0,0);

        if (folder.getLevel()>0) {
            ivIcon.setVisibility(View.INVISIBLE);
        }
        else {
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageDrawable(FolderDrawable.getFolderIcon(context, FolderType.getByInt(folder.getType())));
        }

    }

    public FolderAdapter.OnFolderClick getOnFolderClick() {
        return onFolderClick;
    }

    public void setOnFolderClick(FolderAdapter.OnFolderClick onFolderClick) {
        this.onFolderClick = onFolderClick;
    }
}