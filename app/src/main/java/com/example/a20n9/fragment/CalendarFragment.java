package com.example.a20n9.fragment;

import android.app.AlertDialog;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.a20n9.R;
import com.example.a20n9.activity.CalendarAddActivity;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment {

    ImageButton nextBtn, previousBtn;
    TextView currentDate;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;

    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM", Locale.ENGLISH);

    CalendarGridAdapter calendarGridAdapter;
    List<Date> dates = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewCalendar = (ViewGroup) inflater.inflate(R.layout.calendar_view, container, false);


        nextBtn = viewCalendar.findViewById(R.id.nextBtn);
        previousBtn = viewCalendar.findViewById(R.id.previousBtn);
        currentDate = viewCalendar.findViewById(R.id.currentDate);
        gridView = viewCalendar.findViewById(R.id.gridView);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, 1);
                setupCalendar();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, -1);
                setupCalendar();
            }
        });

        //點選日期
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tvCurrentDay = viewCalendar.findViewById(R.id.currentDate);
                String currentDayStr = tvCurrentDay.getText().toString();

                TextView tvDay = view.findViewById(R.id.calendar_day);
                String dateStr = tvDay.getText().toString();
                int dateInt = Integer.parseInt(dateStr);
                if(dateInt < 10){
                    dateStr = "0" + dateStr;
                }

                dateStr =  currentDayStr +"/"+dateStr;
                Intent intent = new Intent(getActivity(), CalendarAddActivity.class);
                intent.putExtra("date", dateStr);
                startActivityForResult(intent,1);

            }
        });

        setupCalendar();


        return viewCalendar;
    }


    private void setupCalendar() {
        String currentDate_String = dateFormat.format(calendar.getTime());
        currentDate.setText(currentDate_String);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
//        collectEventPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(monthCalendar.DAY_OF_MONTH, 1);
        }

        calendarGridAdapter = new CalendarGridAdapter(getContext(), dates, calendar);
        gridView.setAdapter(calendarGridAdapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        setupCalendar();
    }
}
