package com.example.getpet.Model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.getpet.MyApplication;

@Database(entities = {Pets.class}, version = 4)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PetsDao studentDao();
}

public class AppLocalDB {
    static public final AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
    private AppLocalDB(){}
}


