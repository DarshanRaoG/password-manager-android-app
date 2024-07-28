package com.drg.ironvault.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pwd {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String username;
    private String password;
    private String notes;
    private int userId;

    public Pwd() {
    }

    public Pwd(String title, String username, String password) {
        this.title = title;
        this.username = username;
        this.password = password;
    }

    public Pwd(String title, String username, String password, String notes, int userId) {
        this.title = title;
        this.username = username;
        this.password = password;
        this.notes = notes;
        this.userId = userId;
    }

    public Pwd(int id, String title, String username, String password, String notes, int userId) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.password = password;
        this.notes = notes;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
