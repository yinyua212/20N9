package com.example.a20n9.database.dao;

import com.example.a20n9.database.entity.BasicInfo;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BasicInfoDao {
    @Query("SELECt * FROM BasicInfo")
    List<BasicInfo> getAll();

    @Insert
    void insert(BasicInfo basicInfo);

    @Query("UPDATE BasicInfo SET name_left = :nameLeft")
    void updateNameLeft(String nameLeft);

    @Query("UPDATE BasicInfo SET name_right = :nameRight")
    void updateNameRight(String nameRight);

    @Query("UPDATE BasicInfo SET first_date = :firstDate")
    void updateFirstDate(String firstDate);

    @Query("UPDATE BasicInfo SET show_days = :showDays")
    void updateShowDays(boolean showDays);

}
