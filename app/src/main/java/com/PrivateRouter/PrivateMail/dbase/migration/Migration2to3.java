package com.PrivateRouter.PrivateMail.dbase.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class Migration2to3 extends Migration {

    public Migration2to3() {
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("DELETE FROM storages");
        database.execSQL("ALTER TABLE storages ADD display INTEGER NOT NULL DEFAULT 1");
    }
}