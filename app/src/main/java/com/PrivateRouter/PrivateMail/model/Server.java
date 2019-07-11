package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

public class Server {

    @SerializedName("EntityId")
    private
    int entityId;

    @SerializedName("UUID")
    private
    String uuID;

    @SerializedName("ParentUUID")
    private
    String parentUUID;

    @SerializedName("ModuleName")
    private
    String ModuleName;

    @SerializedName("TenantId")
    private
    int tenantId;

    @SerializedName("Name")
    private
    String name;

    @SerializedName("IncomingServer")
    private
    String incomingServer;

    @SerializedName("IncomingPort")
    private
    int incomingPort;

    @SerializedName("IncomingUseSsl")
    private
    boolean incomingUseSsl;

    @SerializedName("OutgoingServer")
    private
    String outgoingServer;

    @SerializedName("OutgoingPort")
    private
    int outgoingPort;

    @SerializedName("OutgoingUseSsl")
    private
    boolean outgoingUseSsl;

    @SerializedName("SmtpAuthType")
    private
    int smtpAuthType;

    @SerializedName("SmtpLogin")
    private
    String smtpLogin;

    @SerializedName("SmtpPassword")
    private
    String smtpPassword;


    @SerializedName("OwnerType")
    private
    String ownerType;

    @SerializedName("Domains")
    private
    String domains;

    @SerializedName("EnableSieve")
    private
    boolean enableSieve;

    @SerializedName("SievePort")
    private
    int sievePort;


    @SerializedName("EnableThreading")
    private
    boolean enableThreading;

    @SerializedName("UseFullEmailAddressAsLogin")
    private
    boolean useFullEmailAddressAsLogin;


    @SerializedName("SetExternalAccessServers")
    private
    boolean setExternalAccessServers;

    @SerializedName("ExternalAccessImapServer")
    private
    String externalAccessImapServer;

    @SerializedName("ExternalAccessImapPort")
    private
    int externalAccessImapPort;

    @SerializedName("ExternalAccessSmtpServer")
    private
    String externalAccessSmtpServer;

    @SerializedName("ExternalAccessSmtpPort")
    private
    int externalAccessSmtpPort;

    @SerializedName("ServerId")
    private
    int serverId;
}
