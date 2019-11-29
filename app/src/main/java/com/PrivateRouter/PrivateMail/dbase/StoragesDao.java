package com.PrivateRouter.PrivateMail.dbase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.Storages;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface StoragesDao {

    @Query("SELECT * FROM storages")
    List<Storages> getAll();

    @Query("SELECT * FROM storages WHERE id = :id")
    Storages getById(String id);

    @Insert(onConflict = REPLACE)
    void update(Storages storages);

    @Delete
    void delete(Storages storages);
}
