package com.example.a20n9.database.dao;

import com.example.a20n9.database.entity.BasicInfo;
import com.example.a20n9.database.entity.MemoryDay;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MemoryDayDao {
    @Query("SELECT * FROM MemoryDay")
    List<MemoryDay> getAll();

    @Query("SELECT * FROM MemoryDay WHERE days = :days")
    List<MemoryDay> getOneDayByDays(int days);

    @Query("SELECT * FROM MemoryDay WHERE date = :date")
    List<MemoryDay> getOneDayByDate(String date);

    @Query("SELECT EXISTS (SELECT * FROM MemoryDay WHERE date = :date)")
    Boolean isExisted(String date);

    @Insert
    void insert(MemoryDay memoryDay);

    @Query("UPDATE MemoryDay SET date = :date WHERE days = :days")
    void update(String date, int days);

    @Query("DELETE FROM MemoryDay WHERE anniversary = :b")
    void deleteAnniversary(boolean b);

}
