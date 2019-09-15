package com.PrivateRouter.PrivateMail.view.components;

import android.text.Editable;
import android.text.TextWatcher;

public class HostTextWatcher implements TextWatcher {
    private HostEditText hostEditText;
    private final String HTTP_PREFIX = "http://";
    private final String HTTPS_PREFIX = "https://";

    public HostTextWatcher(HostEditText hostEditText){
        this.hostEditText = hostEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.toString().startsWith("http://")){
            hostEditText.setTag(HTTP_PREFIX);

        }else if(s.toString().startsWith("https://")){
            hostEditText.setTag(HTTPS_PREFIX);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().startsWith(HTTP_PREFIX)){
            String hostAddress = s.toString().substring(HTTP_PREFIX.length());
            hostEditText.setText(hostAddress);
        } else if(s.toString().startsWith(HTTPS_PREFIX)){
            String hostAddress = s.toString().substring(HTTPS_PREFIX.length());
            hostEditText.setText(hostAddress);
        }
    }
}
