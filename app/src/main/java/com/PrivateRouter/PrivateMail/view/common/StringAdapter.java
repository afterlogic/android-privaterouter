package com.PrivateRouter.PrivateMail.view.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.PrivateRouter.PrivateMail.view.folders_list.FolderViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StringAdapter  extends RecyclerView.Adapter<StringAdapter.StringViewHolder> {

    private int elementLayoutResId =  R.layout.item_section;
    private final LayoutInflater layoutInflater;
    private Object[] dataSource;
    public StringAdapter(Context context, Object[] dataArgs){
        dataSource = dataArgs;
        layoutInflater = LayoutInflater.from(context);
    }

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public int getElementLayoutResId() {
        return elementLayoutResId;
    }

    public void setElementLayoutResId(int elementLayoutResId) {
        this.elementLayoutResId = elementLayoutResId;
    }

    public interface OnItemClick {
        void onItemClick(int pos, Object item);
    }
    private OnItemClick onItemClick;

    @Override
    public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(elementLayoutResId, parent, false);
        return new StringViewHolder(view);
    }

    public class StringViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_section_name)
        public TextView textView;

        View bg;

        public StringViewHolder(View itemView) {
            super(itemView);
            this.bg = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position, Object s) {
            textView.setText(s.toString());
            bg.setOnClickListener(view -> {
                if (onItemClick!=null)
                    onItemClick.onItemClick(position, s);
            });
            Context context = textView.getContext();



            Drawable drawable = textView.getCompoundDrawables()[2];
            if (drawable!=null) {
                drawable = DrawableCompat.wrap(drawable);
                if (SettingsRepository.getInstance().isNightMode(context))
                    DrawableCompat.setTint(drawable, Color.WHITE);
                else
                    DrawableCompat.setTint(drawable, Color.BLACK);

                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);


                textView.setCompoundDrawables(null, null, drawable, null);
            }

        }

    }

    @Override
    public void onBindViewHolder(StringViewHolder holder, int position) {
        holder.bind(position, dataSource[position]);
    }

    @Override
    public int getItemCount() {
        return dataSource.length;
    }
}