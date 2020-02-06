package com.PrivateRouter.PrivateMail.dbase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.PrivateRouter.PrivateMail.model.Storages;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


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
