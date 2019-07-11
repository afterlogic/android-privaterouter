package com.PrivateRouter.PrivateMail.encryption;

import com.PrivateRouter.PrivateMail.model.Message;

public interface EncryptCallback {
    void onEncrypt(Message message);

    void onFail(String description);
}