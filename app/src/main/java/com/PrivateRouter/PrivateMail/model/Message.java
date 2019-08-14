package com.PrivateRouter.PrivateMail.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.PrivateRouter.PrivateMail.dbase.ArrayStringConverter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "messages", primaryKeys = {"uid", "folder"} )
public class Message extends  MessageBase implements Serializable {

    @NonNull
    @SerializedName("Folder")
    private
    String folder;

    @SerializedName("Subject")
    private
    String subject;

    @SerializedName("MessageId")
    private
    String messageId;

    @SerializedName("Size")
    private
    int size;

    @SerializedName("TextSize")
    private
    int textSize;

    @SerializedName("InternalTimeStampInUTC")
    private
    long internalTimeStampInUTC;

    @SerializedName("ReceivedOrDateTimeStampInUTC")
    private
    long receivedOrDateTimeStampInUTC;

    @SerializedName("TimeStampInUTC")
    private
    long timeStampInUTC;

    @Embedded(prefix = "from")
    @SerializedName("From")
    private
    EmailCollection from;

    @Embedded(prefix = "to")
    @SerializedName("To")
    private
    EmailCollection to;

    @Embedded(prefix = "cc")
    @SerializedName("Cc")
    private
    EmailCollection cc;

    @Embedded(prefix = "bcc")
    @SerializedName("Bcc")
    private
    EmailCollection bcc;

    @Embedded(prefix = "sender")
    @SerializedName("Sender")
    private
    EmailCollection sender;

    @Embedded(prefix = "replyto")
    @SerializedName("ReplyTo")
    private
    EmailCollection replyTo;


    @SerializedName("HasAttachments")
    private
    boolean hasAttachments;

    @SerializedName("HasVcardAttachment")
    private
    boolean hasVcardAttachment;

    @SerializedName("HasIcalAttachment")
    private
    boolean hasIcalAttachment;

    @SerializedName("Importance")
    private
    int importance;

    @TypeConverters({ArrayStringConverter.class})
    @SerializedName("DraftInfo")
    private
    ArrayList<String> draftInfo;

    @SerializedName("Sensitivity")
    private
    int sensitivity;

    @SerializedName("TrimmedTextSize")
    private
    int trimmedTextSize;

    @SerializedName("DownloadAsEmlUrl")
    private
    String downloadAsEmlUrl;

    @SerializedName("Hash")
    private
    String hash;


    @SerializedName("Headers")
    private
    String headers;

    @SerializedName("InReplyTo")
    private
    String inReplyTo;

    @SerializedName("References")
    private
    String references;

    @SerializedName("ReadingConfirmationAddressee")
    private
    String readingConfirmationAddressee;


    @SerializedName("Html")
    private
    String html;

    @SerializedName("Trimmed")
    private
    boolean trimmed;

    @SerializedName("Plain")
    private
    String plain;


    @SerializedName("PlainRaw")
    private
    String plainRaw;

    @SerializedName("Rtl")
    private
    boolean rtl;

    @SerializedName("Safety")
    private
    boolean safety;

    @SerializedName("HasExternals")
    private
    boolean hasExternals;


    @Embedded(prefix = "attachments")
    @SerializedName("Attachments")
    private
    AttachmentCollection attachments;


    @Ignore
    private
    List<Message> threadList;

    //TODO FoundedCIDs,  FoundedContentLocationUrls, Custom Extend



    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public long getInternalTimeStampInUTC() {
        return internalTimeStampInUTC;
    }

    public void setInternalTimeStampInUTC(long internalTimeStampInUTC) {
        this.internalTimeStampInUTC = internalTimeStampInUTC;
    }

    public long getReceivedOrDateTimeStampInUTC() {
        return receivedOrDateTimeStampInUTC;
    }

    public void setReceivedOrDateTimeStampInUTC(long receivedOrDateTimeStampInUTC) {
        this.receivedOrDateTimeStampInUTC = receivedOrDateTimeStampInUTC;
    }

    public long getTimeStampInUTC() {
        return timeStampInUTC;
    }

    public void setTimeStampInUTC(long timeStampInUTC) {
        this.timeStampInUTC = timeStampInUTC;
    }

    public EmailCollection getFrom() {
        return from;
    }

    public void setFrom(EmailCollection from) {
        this.from = from;
    }

    public EmailCollection getTo() {
        return to;
    }

    public void setTo(EmailCollection to) {
        this.to = to;
    }

    public EmailCollection getCc() {
        return cc;
    }

    public void setCc(EmailCollection cc) {
        this.cc = cc;
    }

    public EmailCollection getBcc() {
        return bcc;
    }

    public void setBcc(EmailCollection bcc) {
        this.bcc = bcc;
    }

    public EmailCollection getSender() {
        return sender;
    }

    public void setSender(EmailCollection sender) {
        this.sender = sender;
    }

    public EmailCollection getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(EmailCollection replyTo) {
        this.replyTo = replyTo;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public boolean isHasVcardAttachment() {
        return hasVcardAttachment;
    }

    public void setHasVcardAttachment(boolean hasVcardAttachment) {
        this.hasVcardAttachment = hasVcardAttachment;
    }

    public boolean isHasIcalAttachment() {
        return hasIcalAttachment;
    }

    public void setHasIcalAttachment(boolean hasIcalAttachment) {
        this.hasIcalAttachment = hasIcalAttachment;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public ArrayList<String> getDraftInfo() {
        return draftInfo;
    }

    public void setDraftInfo(ArrayList<String> draftInfo) {
        this.draftInfo = draftInfo;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public int getTrimmedTextSize() {
        return trimmedTextSize;
    }

    public void setTrimmedTextSize(int trimmedTextSize) {
        this.trimmedTextSize = trimmedTextSize;
    }

    public String getDownloadAsEmlUrl() {
        return downloadAsEmlUrl;
    }

    public void setDownloadAsEmlUrl(String downloadAsEmlUrl) {
        this.downloadAsEmlUrl = downloadAsEmlUrl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getReadingConfirmationAddressee() {
        return readingConfirmationAddressee;
    }

    public void setReadingConfirmationAddressee(String readingConfirmationAddressee) {
        this.readingConfirmationAddressee = readingConfirmationAddressee;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public boolean isTrimmed() {
        return trimmed;
    }

    public void setTrimmed(boolean trimmed) {
        this.trimmed = trimmed;
    }

    public String getPlain() {
        return plain;
    }

    public void setPlain(String plain) {
        this.plain = plain;
    }

    public String getPlainRaw() {
        return plainRaw;
    }

    public void setPlainRaw(String plainRaw) {
        this.plainRaw = plainRaw;
    }

    public boolean isRtl() {
        return rtl;
    }

    public void setRtl(boolean rtl) {
        this.rtl = rtl;
    }

    public boolean isSafety() {
        return safety;
    }

    public void setSafety(boolean safety) {
        this.safety = safety;
    }

    public boolean isHasExternals() {
        return hasExternals;
    }

    public void setHasExternals(boolean hasExternals) {
        this.hasExternals = hasExternals;
    }

    public AttachmentCollection getAttachments() {
        return attachments;
    }

    public void setAttachments(AttachmentCollection attachments) {
        this.attachments = attachments;
    }

    public List<Message> getThreadList() {
        return threadList;
    }

    public void setThreadList(List<Message> threadList) {
        this.threadList = threadList;
    }

    public boolean isThreadMessage() {
        return  getParentUid()!=0;
    }
}
