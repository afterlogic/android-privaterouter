package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class Account implements Serializable {

    @SerializedName("EntityId")
    private
    String entityId;

    @SerializedName("UUID")
    private
    String uuID;

    @SerializedName("ParentUUID")
    private
    String parentUUID;

    @SerializedName("ModuleName")
    private
    String moduleName;

    @SerializedName("IsDisabled")
    private
    boolean isDisabled;


    @SerializedName("IdUser")
    private
    int idUser;

    @SerializedName("UseToAuthorize")
    private
    boolean useToAuthorize;

    @SerializedName("Email")
    private
    String email;

    @SerializedName("FriendlyName")
    private
    String friendlyName;

    @SerializedName("IncomingLogin")
    private
    String incomingLogin;


    @SerializedName("UseSignature")
    private
    boolean useSignature;

    @SerializedName("Signature")
    private
    String signature;

    @SerializedName("ServerId")
    private
    int serverId;

    @SerializedName("FoldersOrder")
    private
    String foldersOrder;

    @SerializedName("UseThreading")
    private
    boolean useThreading;

    @SerializedName("SaveRepliesToCurrFolder")
    private
    boolean saveRepliesToCurrFolder;

    @SerializedName("AccountID")
    private
    int accountID;

    @SerializedName("CanBeUsedToAuthorize")
    private
    boolean canBeUsedToAuthorize;


    private
    FolderCollection folders;


    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getUuID() {
        return uuID;
    }

    public void setUuID(String uuID) {
        this.uuID = uuID;
    }

    public String getParentUUID() {
        return parentUUID;
    }

    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public boolean isUseToAuthorize() {
        return useToAuthorize;
    }

    public void setUseToAuthorize(boolean useToAuthorize) {
        this.useToAuthorize = useToAuthorize;
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

    public String getIncomingLogin() {
        return incomingLogin;
    }

    public void setIncomingLogin(String incomingLogin) {
        this.incomingLogin = incomingLogin;
    }

    public boolean isUseSignature() {
        return useSignature;
    }

    public void setUseSignature(boolean useSignature) {
        this.useSignature = useSignature;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getFoldersOrder() {
        return foldersOrder;
    }

    public void setFoldersOrder(String foldersOrder) {
        this.foldersOrder = foldersOrder;
    }

    public boolean isUseThreading() {
        return useThreading;
    }

    public void setUseThreading(boolean useThreading) {
        this.useThreading = useThreading;
    }

    public boolean isSaveRepliesToCurrFolder() {
        return saveRepliesToCurrFolder;
    }

    public void setSaveRepliesToCurrFolder(boolean saveRepliesToCurrFolder) {
        this.saveRepliesToCurrFolder = saveRepliesToCurrFolder;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public boolean isCanBeUsedToAuthorize() {
        return canBeUsedToAuthorize;
    }

    public void setCanBeUsedToAuthorize(boolean canBeUsedToAuthorize) {
        this.canBeUsedToAuthorize = canBeUsedToAuthorize;
    }

    public FolderCollection getFolders() {
        return folders;
    }

    public ArrayList<Folder> getFoldersWithSubFolder( ) {
        return getFoldersWithSubFolder( folders.getCollection(), 0 );
    }
    private ArrayList<Folder> getFoldersWithSubFolder(ArrayList<Folder>  folders, int level) {
        ArrayList<Folder> returnArray = new ArrayList<>();

        for (Folder folder : folders ) {
            folder.setLevel(level);
            if (folder.isSubscribed()) {
                returnArray.add(folder);

                if (folder.getSubFolders() != null && folder.getSubFolders().getCollection() != null) {
                    ArrayList<Folder> rec = getFoldersWithSubFolder(folder.getSubFolders().getCollection(), level + 1);
                    returnArray.addAll(rec);
                }
            }

        }

        return returnArray;
    }



    public void setFolders(FolderCollection folders) {
        this.folders = folders;
    }
}
