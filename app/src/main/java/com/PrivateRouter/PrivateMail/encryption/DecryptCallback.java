package com.PrivateRouter.PrivateMail.encryption;

import com.PrivateRouter.PrivateMail.model.Message;

public interface DecryptCallback {
    void onDecrypt(Message message);
    void onFail(String description);
}
