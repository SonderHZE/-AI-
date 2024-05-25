package com.example.mygptapp.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mygptapp.pojo.UserEntity;


@Dao
public interface UserDao {
    @Insert
    void insert(UserEntity userEntity);

    // 获取最近登录的用户
    @Query("SELECT * FROM user")
    UserEntity getUser();

    @Query("SELECT * FROM user WHERE userID = :userID LIMIT 1")
    UserEntity getUserById(int userID);

    @Query("SELECT token FROM user")
    String getToken();

    @Query("DELETE FROM user")
    void deleteAll();

    // 更新用户信息
    @Update
    void update(UserEntity userEntity);

}