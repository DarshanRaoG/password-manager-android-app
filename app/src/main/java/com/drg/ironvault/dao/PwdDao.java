package com.drg.ironvault.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import com.drg.ironvault.entity.Pwd;

import java.util.List;

@Dao
public interface PwdDao {
    @Upsert
    void upsert(Pwd pwd);

    @Delete
    void delete(Pwd pwd);

    @Query("SELECT * FROM PWD WHERE userId = :userId ORDER BY LOWER(title), LOWER(username)")
    List<Pwd> getPwdList(int userId);

    @Query("SELECT * FROM PWD WHERE id = :id")
    Pwd getPwdById(int id);

    @Query("DELETE FROM PWD WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM PWD WHERE userId = :userId AND (title LIKE '%' || :searchText || '%' OR username LIKE '%' || :searchText || '%') ORDER BY title, username")
    List<Pwd> search(int userId, String searchText);
}
