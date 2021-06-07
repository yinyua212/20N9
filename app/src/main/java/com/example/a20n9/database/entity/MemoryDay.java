package com.example.a20n9.database.entity;

import java.util.Date;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MemoryDay {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "days")
    public int days;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "mark")
    public String mark;

    @ColumnInfo(name = "anniversary")
    public boolean anniversary;
}
