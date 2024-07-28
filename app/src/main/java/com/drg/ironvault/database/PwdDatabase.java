package com.drg.ironvault.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.drg.ironvault.dao.PwdDao;
import com.drg.ironvault.entity.Pwd;

@Database(entities = {Pwd.class}, version = 1)
public abstract class PwdDatabase extends RoomDatabase {
    public abstract PwdDao pwdDao();

    private static volatile PwdDatabase INSTANCE;

    public static PwdDatabase getINSTANCE(Context context) {
        if(INSTANCE == null) {
            synchronized (PwdDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, PwdDatabase.class, "pwd_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
