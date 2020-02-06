package com.PrivateRouter.PrivateMail.dbase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.FolderHash;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.Storages;
import com.PrivateRouter.PrivateMail.model.TempMessageIds;

@Database(entities = {Message.class, FolderHash.class, Contact.class, TempMessageIds.class, Storages.class},
        version = 3, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
    public abstract StoragesDao storagesDao();
}
