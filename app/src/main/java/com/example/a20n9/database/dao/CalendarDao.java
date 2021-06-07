package com.example.a20n9.database.dao;

import com.example.a20n9.database.entity.CalendarNote;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CalendarDao {
    @Query("SELECT * FROM CalendarNote WHERE date = :date")
    CalendarNote getCalendarNoteByDate(String date);

    @Insert
    void insert(CalendarNote calendarNote);

    @Query("UPDATE CalendarNote SET content = :content, address = :address WHERE date = :date")
    void update(String date, String content, String address);
}
