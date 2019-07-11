package com.PrivateRouter.PrivateMail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Attachments implements Serializable {

    @SerializedName("FileName")
    private
    String fileName;


    @SerializedName("MimeType")
    private
    String mimeType;

    @SerializedName("MimePartIndex")
    private
    String mimePartIndex;


    @SerializedName("TempName")
    private
    String tempName;

    @SerializedName("EstimatedSize")
    private
    int estimatedSize;


    @SerializedName("Hash")
    private
    String hash;


    @SerializedName("Actions")
    private
    Actions actions;

    @SerializedName("CID")
    private
    String CID;

    @SerializedName("ContentLocation")
    private
    String contentLocation;

    @SerializedName("Content")
    private
    String content;

    @SerializedName("ThumbnailUrl")
    private
    String thumbnailUrl;

    @SerializedName("IsInline")
    private
    boolean isInline;

    @SerializedName("IsLinked")
    private
    boolean isLinked;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimePartIndex() {
        return mimePartIndex;
    }

    public void setMimePartIndex(String mimePartIndex) {
        this.mimePartIndex = mimePartIndex;
    }

    public int getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(int estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getContentLocation() {
        return contentLocation;
    }

    public void setContentLocation(String contentLocation) {
        this.contentLocation = contentLocation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean isInline() {
        return isInline;
    }

    public void setInline(boolean inline) {
        isInline = inline;
    }

    public boolean isLinked() {
        return isLinked;
    }

    public void setLinked(boolean linked) {
        isLinked = linked;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public class Actions implements Serializable {

        @SerializedName("view")
        private
        UrlWrapper view;

        @SerializedName("download")
        private
        UrlWrapper download;

        public UrlWrapper getView() {
            return view;
        }

        public void setView(UrlWrapper view) {
            this.view = view;
        }

        public UrlWrapper getDownload() {
            return download;
        }

        public void setDownload(UrlWrapper download) {
            this.download = download;
        }


        public class UrlWrapper implements Serializable {

            @SerializedName("url")
            private
            String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }

}
