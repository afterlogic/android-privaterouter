package com.PrivateRouter.PrivateMail.view.settings;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.PGPKey;

import java.util.List;

public class PGPKeyAdapter  extends RecyclerView.Adapter<PGPKeyViewHolder> {
    private List<PGPKey> pgpKeys;
    private LayoutInflater layoutInflater;
    private PGPKeyViewHolder.OnChecked onChecked;


    public PGPKeyAdapter(Context context, List<PGPKey> pgpKeys){
        this.pgpKeys = pgpKeys;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PGPKeyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_import_keys, viewGroup, false);
        PGPKeyViewHolder pgpKeyViewHolder = new PGPKeyViewHolder(view);
        pgpKeyViewHolder.setOnChecked(onChecked);
        return pgpKeyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PGPKeyViewHolder pgpKeyViewHolder, int i) {
        PGPKey PGPKey = pgpKeys.get(i);
        pgpKeyViewHolder.bind(PGPKey);
    }

    @Override
    public int getItemCount() {
        if (pgpKeys == null){
            return 0;
        } else
            return pgpKeys.size();
    }


    public PGPKeyViewHolder.OnChecked getOnChecked() {
        return onChecked;
    }

    public void setOnChecked(PGPKeyViewHolder.OnChecked onChecked) {
        this.onChecked = onChecked;
    }
}
