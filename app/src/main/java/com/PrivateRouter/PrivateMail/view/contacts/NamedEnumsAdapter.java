package com.PrivateRouter.PrivateMail.view.contacts;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.model.NamedEnums;

import java.util.List;

public class NamedEnumsAdapter extends ArrayAdapter<NamedEnums> {

    private List<NamedEnums> namedEnums;
    private List<EditText> etList;

    public NamedEnumsAdapter(Activity context, int textViewResourceId, List<NamedEnums> namedEnums, List<EditText> etList) {
        super(context, textViewResourceId, namedEnums);
        this.namedEnums = namedEnums;
        this.etList = etList;
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
        String title = namedEnums.getName();
        if(etList.size() > position){
            title = title  + ": " + etList.get(position).getText().toString();
        }
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(title);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setText(namedEnums.get(position).getName());
        return textView;
    }
}
