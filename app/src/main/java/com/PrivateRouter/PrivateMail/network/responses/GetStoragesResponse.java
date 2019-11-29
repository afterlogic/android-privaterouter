package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Identities;
import com.PrivateRouter.PrivateMail.model.Storages;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetStoragesResponse extends BaseResponse{
    @SerializedName("Result")
    private List<Storages> storages;

    public List<Storages> getStorages() {
        return storages;
    }

    public void setStorages(List<Storages> storages) {
        this.storages = storages;
    }
}
