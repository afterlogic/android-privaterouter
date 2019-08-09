package com.PrivateRouter.PrivateMail.view.contacts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.PrivateRouter.PrivateMail.R;
import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.mail_list.MailListAdapter;
import com.PrivateRouter.PrivateMail.view.utils.DateUtils;
import com.PrivateRouter.PrivateMail.view.utils.EmailUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_email)
    TextView tvEmail;

    @BindView(R.id.cb_selected)
    CheckBox cbSelected;

    boolean checked;

    Contact contact;

    private ContactsAdapter.OnContactClick onContactClick;
    private int position;
    private ContactsAdapter contactsAdapter;

    @SuppressWarnings("unused")
    @OnClick(R.id.cl_background)
    public void bgImageClick() {
        cbSelected.setChecked(!cbSelected.isChecked());
    }


    public void onCheckedChange(CompoundButton button, boolean value) {
        contactsAdapter.onSelectChange(value, contact);
    }


    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }


    @SuppressWarnings("unused")
    @OnCheckedChanged(R.id.cb_selected)
    public void bgCheckClick(boolean checked) {
        this.checked = checked;
    }

    @SuppressWarnings("unused")
    @OnLongClick(R.id.cl_background)
    public boolean bgImageLongClick() {
        contactsAdapter.onLongTap();
        return true;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cl_background)
    public void bgClick() {
        if (onContactClick != null) {
            onContactClick.onContactClick(contact, position);
        }
    }


    public void bind(Contact contact, int position, ContactsAdapter contactsAdapter, boolean selectionMode, boolean checked) {
        this.checked = checked;
        this.position = position;
        this.contact = contact;
        this.contactsAdapter = contactsAdapter;

        if (contact != null) {

            if (selectionMode) {

                cbSelected.setVisibility(View.VISIBLE);
                cbSelected.setChecked(checked);
                cbSelected.setOnCheckedChangeListener(this::onCheckedChange);
            } else {
                cbSelected.setVisibility(View.INVISIBLE);
            }
            tvEmail.setText(contact.getViewEmail());
            tvName.setText(contact.getFullName());

        } else {
            tvEmail.setText("");
            tvName.setText("");
            cbSelected.setVisibility(View.INVISIBLE);
        }
    }

    public ContactsAdapter.OnContactClick getOnContactClick() {
        return onContactClick;
    }

    public void setOnContactClick(ContactsAdapter.OnContactClick onContactClick) {
        this.onContactClick = onContactClick;
    }


}
