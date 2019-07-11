package com.PrivateRouter.PrivateMail.dbase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.FolderHash;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.StorageCTag;

@Database(entities = {Message.class, FolderHash.class, Contact.class, StorageCTag.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
}
