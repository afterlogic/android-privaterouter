package com.PrivateRouter.PrivateMail.model;

import java.io.Serializable;

public class FolderMeta implements Serializable {

    private int count;
    private int unreadCount;
    private String nextUids;
    private String hash;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getNextUids() {
        return nextUids;
    }

    public void setNextUids(String nextUids) {
        this.nextUids = nextUids;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
