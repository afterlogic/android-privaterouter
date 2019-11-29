package com.PrivateRouter.PrivateMail.view.contacts;

import com.PrivateRouter.PrivateMail.model.Storages;

import java.util.List;

public  interface StoragesCallback {
    void onStorages(List<Storages> storages);
}