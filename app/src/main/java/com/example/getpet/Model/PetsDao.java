package com.example.getpet.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PetsDao {
    @Query("select * from Pets")
    List<Pets> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Pets... pets);

    @Delete
    void delete(Pets pets);

    @Query("SELECT * FROM Pets WHERE id=:id ")
    Pets getPet(String id);
}
