package com.PrivateRouter.PrivateMail.dbase.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class Migration1to2 extends Migration {

    public Migration1to2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

        database.execSQL("drop table storage_c_tag");
        database.execSQL("create table storages (id TEXT NOT NULL, name TEXT,cTag INTEGER NOT NULL, PRIMARY KEY(id))");

    }
}
