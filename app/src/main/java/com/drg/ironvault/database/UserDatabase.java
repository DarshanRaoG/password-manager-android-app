package com.drg.ironvault.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.drg.ironvault.dao.UserDao;
import com.drg.ironvault.entity.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase{
    public abstract UserDao userDao();

    private static volatile UserDatabase INSTANCE;

    public static UserDatabase getINSTANCE(Context context) {
        if(INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room
                                    .databaseBuilder(context.getApplicationContext(), UserDatabase.class, "user_db")
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
