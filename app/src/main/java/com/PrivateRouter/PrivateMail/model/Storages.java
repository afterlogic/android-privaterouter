package com.PrivateRouter.PrivateMail.model;

public enum Storages {
    ALL("personal"),
    PERSONAL("personal"),
    TEAM("team"),
    SHARED("shared");

    private final String id;

    Storages(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}