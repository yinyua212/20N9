package com.example.a20n9.fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a20n9.R;
import com.example.a20n9.database.AppDataBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

public class CalendarGridAdapter extends ArrayAdapter {

    Context context;
    List<Date> dates;
    Calendar currentDate;
    LayoutInflater inflater;

    AppDataBase db;

    public CalendarGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate) {
        super(context, R.layout.single_cell_layout);
        this.context = context;
        this.dates = dates;
        this.currentDate = currentDate;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.single_cell_layout, parent,false);
        }

        //本月顯示顏色
        if (displayMonth == currentMonth && displayYear == currentYear) {
            view.setBackgroundColor(Color.parseColor("#BB86FC"));
        } else {
            view.setBackgroundColor(Color.parseColor("#cccccc"));
        }

        //如果有做過紀錄，顯示愛心
        if(db == null){
            db = Room.databaseBuilder(getContext(), AppDataBase.class, "db1").allowMainThreadQueries().build();
        }

        TextView day_Number = view.findViewById(R.id.calendar_day);
        ImageView note = view.findViewById(R.id.tvNote);
        day_Number.setText(String.valueOf(dayNo));

        String monthStr = displayMonth < 10 ? "0" + displayMonth : "" + displayMonth;
        String date = displayYear +"/" + monthStr + "/" +dayNo;
        if(db.calendarDao().getCalendarNoteByDate(date) != null){
            note.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.redheart));
        }

        //本日顯示顏色
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date today = Calendar.getInstance().getTime();
        if(sdf.format(today).equals(date)){
            day_Number.setTextColor(Color.parseColor("#9400D3"));
        }



        return view;
    }

    private Date convertStringToDate(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(eventDate);
        }catch (ParseException e ){
            e.printStackTrace();
        }

        return date;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}
