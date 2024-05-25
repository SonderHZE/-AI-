package com.example.mygptapp.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mygptapp.Dao.UserDao;
import com.example.mygptapp.pojo.UserEntity;


@Database(entities = {UserEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}