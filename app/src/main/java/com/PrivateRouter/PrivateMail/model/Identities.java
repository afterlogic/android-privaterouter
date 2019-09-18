package com.PrivateRouter.PrivateMail.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Identities {
    @SerializedName("EntityId")
    private
    int entityId;

    @SerializedName("Email")
    private
    String email;

    @SerializedName("FriendlyName")
    private
    String friendlyName;

    @SerializedName("UseSignature")
    private
    boolean useSignature;


    @SerializedName("Default")
    private
    boolean isDefault;

    @NonNull
    @Override
    public String toString() {
        return friendlyName + " <"+ email +">";
    }

    public Identities(String email, String friendlyName) {
        this.email = email;
        this.friendlyName = friendlyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public boolean isUseSignature() {
        return useSignature;
    }

    public void setUseSignature(boolean useSignature) {
        this.useSignature = useSignature;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
