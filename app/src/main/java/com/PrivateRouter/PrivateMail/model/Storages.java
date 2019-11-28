package com.PrivateRouter.PrivateMail.model;

public enum Storages {
    PERSONAL("personal"),
    TEAM("team");

    private final String id;

    Storages(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
