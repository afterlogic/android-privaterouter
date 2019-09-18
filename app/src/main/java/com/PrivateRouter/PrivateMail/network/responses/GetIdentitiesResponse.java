package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Identities;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetIdentitiesResponse   extends BaseResponse {
    @SerializedName("Result")
    private ArrayList<Identities> identities;

    public ArrayList<Identities> getIdentities() {
        return identities;
    }

    public void setIdentities(ArrayList<Identities> identities) {
        this.identities = identities;
    }
}