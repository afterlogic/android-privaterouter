package com.PrivateRouter.PrivateMail.model;

public enum FolderType {

    Inbox(1),
    Sent(2),
    Drafts(3),
    Spam(4),
    Trash(5),
    User(10);


    int id;
    FolderType(int id) {
        this.id = id;
    }

    public  int getId() {return id;}

    public static FolderType getByInt(int val) {
        for (FolderType folderType: values()) {
            if (folderType.id == val)
                return folderType;
        }
        return User;
    }
}
