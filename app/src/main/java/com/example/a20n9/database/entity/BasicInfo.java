package com.example.a20n9.database.entity;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BasicInfo {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name_left")
    public String nameLeft;

    @ColumnInfo(name = "name_right")
    public String nameRight;

    @ColumnInfo(name = "first_date")
    public String firstDate;

    @ColumnInfo(name ="show_days")
    public Boolean showDays;
}
