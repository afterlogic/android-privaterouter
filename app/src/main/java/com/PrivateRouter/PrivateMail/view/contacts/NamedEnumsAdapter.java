package com.PrivateRouter.PrivateMail.view.contacts;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.model.NamedEnums;

import java.util.List;

public class NamedEnumsAdapter extends ArrayAdapter<NamedEnums> {

    private List<NamedEnums> namedEnums;

    public NamedEnumsAdapter(Activity context, int textViewResourceId, List<NamedEnums> namedEnums) {
        super(context, textViewResourceId, namedEnums);
        this.namedEnums = namedEnums;
    }

    @Override
    public int getCount() {
        return namedEnums.size();
    }

    @Override
    public NamedEnums getItem(int position) {
        return namedEnums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NamedEnums namedEnums = getItem(position);
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(namedEnums.getName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setText(namedEnums.get(position).getName());
        return textView;
    }
}
