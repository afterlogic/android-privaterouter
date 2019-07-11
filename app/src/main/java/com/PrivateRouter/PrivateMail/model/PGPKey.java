package com.PrivateRouter.PrivateMail.model;

import java.io.Serializable;

public class PGPKey implements Serializable {
    private String userID;
    private int strength;
    private Object keyObject;
    private long id;


    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return userID;
    }
    public void setType(String type) {
        this.type = type;
    }

    private String type;


    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public Object getKeyObject() {
        return keyObject;
    }

    public void setKeyObject(Object keyObject) {
        this.keyObject = keyObject;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
