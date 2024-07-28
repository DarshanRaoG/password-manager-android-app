package com.drg.ironvault.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import com.drg.ironvault.entity.User;

@Dao
public interface UserDao {
    @Upsert
    void upsert(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM USER WHERE id = :id")
    User getUserById(int id);

    @Query("SELECT * FROM USER WHERE LOWER(username) LIKE LOWER(:username)")
    User verifyUser(String username);

    @Query("SELECT 1 FROM USER WHERE LOWER(username) LIKE LOWER(:username) AND LOWER(password) LIKE LOWER(:password)")
    boolean validateUser(String username, String password);
}
