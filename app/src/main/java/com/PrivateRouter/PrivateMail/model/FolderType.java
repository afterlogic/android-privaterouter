package com.PrivateRouter.PrivateMail.model;

public enum FolderType {

    Inbox(1),
    Sent(2),
    Drafts(3),
    Spam(4),
    Trash(5),
    User(10),
    VirtualStarred(-10);


    int id;
    FolderType(int id) {
        this.id = id;
    }

    public  int getId() {return id;}

    public static final String VIRTUAL_STARRED_NAME = "Starred";
    public static FolderType getByInt(int val) {
        for (FolderType folderType: values()) {
            if (folderType.id == val)
                return folderType;
        }
        return User;
    }
}
