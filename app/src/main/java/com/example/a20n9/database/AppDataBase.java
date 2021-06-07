package com.example.a20n9.database;

import com.example.a20n9.database.dao.BasicInfoDao;
import com.example.a20n9.database.dao.CalendarDao;
import com.example.a20n9.database.dao.MemoryDayDao;
import com.example.a20n9.database.entity.BasicInfo;
import com.example.a20n9.database.entity.CalendarNote;
import com.example.a20n9.database.entity.MemoryDay;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BasicInfo.class, MemoryDay.class, CalendarNote.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract BasicInfoDao basicInfoDao();
    public abstract MemoryDayDao memoryDayDao();
    public abstract CalendarDao calendarDao();
}
