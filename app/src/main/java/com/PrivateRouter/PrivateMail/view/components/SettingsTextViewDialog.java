package com.PrivateRouter.PrivateMail.view.components;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.encryption.ImportTask;
import com.PrivateRouter.PrivateMail.model.PGPKey;
import com.PrivateRouter.PrivateMail.view.common.StringAdapter;
import com.PrivateRouter.PrivateMail.view.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsTextViewDialog  extends DialogFragment   {

    @BindView(R.id.rv_variants)
    RecyclerView rvVariants;

    String [] variants;

    public OnSelectInterface getOnSelectInterface() {
        return onSelectInterface;
    }

    public void setOnSelectInterface(OnSelectInterface onSelectInterface) {
        this.onSelectInterface = onSelectInterface;
    }

    public void setVariants(String [] variants) {
        this.variants = variants;
    }


    public interface OnSelectInterface
    {
        void onSelect(int index, String value);
    }

    private OnSelectInterface onSelectInterface;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_settings_variants, null);
        ButterKnife.bind(this, view);

        StringAdapter stringAdapter = new StringAdapter(getContext(), variants);
        stringAdapter.setElementLayoutResId(R.layout.item_settings_variant);
        stringAdapter.setOnItemClick((pos, item) -> {
            if (onSelectInterface!=null)
                onSelectInterface.onSelect(pos, item.toString());
            dismiss();
        });
        rvVariants.setAdapter( stringAdapter );


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.dialog_relative_width, outValue, true);
        float relativeWidth = outValue.getFloat();

        int width = (int)(Utils.getDeviceMetrics(getActivity()).widthPixels * relativeWidth);
        int maxHeight = (int)(Utils.getDeviceMetrics(getActivity()).heightPixels* relativeWidth);

        int expectedHeight = maxHeight;
        if (variants!=null) {
            expectedHeight = Utils.getDP(getContext(), 108 + variants.length*55 );
        }
        getDialog().getWindow().setLayout(  width,  Math.min(maxHeight , expectedHeight) ) ;
    }

}
