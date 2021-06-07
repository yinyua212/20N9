package com.example.a20n9.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a20n9.R;
import com.example.a20n9.database.AppDataBase;
import com.example.a20n9.database.entity.BasicInfo;
import com.example.a20n9.database.entity.MemoryDay;
import com.example.a20n9.utility.AlertReceiver;
import com.google.android.gms.common.internal.Objects;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

public class MemoryDayFragment extends Fragment {

    ViewGroup viewMemoryDay;
    AppDataBase db;
    private ListView listView;
    int[] dateToRemind = new int[]{};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewMemoryDay == null) {

            viewMemoryDay = (ViewGroup) inflater.inflate(R.layout.memory_day_view, container, false);
            listView = viewMemoryDay.findViewById(R.id.listViewMemoryDay);

            //floating button
            FloatingActionButton fab = viewMemoryDay.findViewById(R.id.btnFloat);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View v = inflater.inflate(R.layout.alertdialog_add_memory_day, container, false);
                    TextView tvDate = v.findViewById(R.id.tvDate);
                    tvDate.setPaintFlags(tvDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    EditText etMark = v.findViewById(R.id.etMark);

                    Date today = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    tvDate.setText(sdf.format(today));

                    //點選日期產生月曆
                    tvDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Date d = null;
                            try {
                                d = sdf.parse(tvDate.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(d);
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                    String chosenDate = y + "/" + (m + 1) + "/" + d;
                                    try {
                                        chosenDate = sdf.format(sdf.parse(chosenDate));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    tvDate.setText(chosenDate);
                                }
                            }, year, month, day);
//                            datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTime().getTime());
                            datePickerDialog.show();
                        }
                    });


                    new AlertDialog.Builder(view.getContext())
                            .setTitle("請輸入紀念日")
                            .setView(v)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CheckBox cbRepeat = v.findViewById(R.id.cbRepeat);
                                    String mark = etMark.getText().toString();
                                    String date = tvDate.getText().toString();

                                    if (db.memoryDayDao().isExisted(date)) {
                                        Toast.makeText(getContext(), "此紀念日已存在", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if (mark.length() > 0) {
                                        List<BasicInfo> basinInfoList = db.basicInfoDao().getAll();
                                        String firstDateStr = basinInfoList.get(0).firstDate;
                                        int repeat = 1;
                                        if (cbRepeat.isChecked())
                                            repeat = 100;

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                        Date newMemoryDate = null;
                                        Date firstDate = null;

                                        for (int i = 0; i < repeat; i++) {
                                            try {
                                                firstDate = sdf.parse(firstDateStr);
                                                newMemoryDate = sdf.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            Calendar c = Calendar.getInstance();
                                            c.setTime(newMemoryDate);
                                            c.add(Calendar.YEAR, i);

                                            if (!db.memoryDayDao().isExisted(sdf.format(c.getTime())))
                                                addMemoryDay(mark, sdf.format(c.getTime()), c.getTime(), firstDate);
                                        }

                                        go();

                                    } else {
                                        Toast.makeText(getContext(), "請輸入紀念日名稱", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setCancelable(true)
                            .show();
                }
            });

            go();
        }
        return viewMemoryDay;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            go();
        }
    }


    public void go() {
        if (db == null) {
            db = Room.databaseBuilder(viewMemoryDay.getContext(), AppDataBase.class, "db1").allowMainThreadQueries().build();
        }


        List<BasicInfo> basinInfoList = db.basicInfoDao().getAll();

        String firstDateStr = basinInfoList.get(0).firstDate;
        if (firstDateStr.length() == 0) {
            Snackbar.make(viewMemoryDay, "請先設定紀念日", Snackbar.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date firstDate = Calendar.getInstance().getTime();
        try {
            firstDate = sdf.parse(firstDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<MemoryDay> memoryDays = db.memoryDayDao().getAll();
        if (memoryDays.size() == 0) {




            dateToRemind = new int[100];
            for (int i = 1; i <= 100; i++) {
                dateToRemind[i - 1] = i * 100;
            }

//            //數字紀念日
//            for (int d : dateToRemind) {
//                MemoryDay memoryDay = new MemoryDay();
//                memoryDay.days = d;
//                memoryDay.mark = d + "天";
//                memoryDay.anniversary = false;
//
//                //在一起的第1天其實是第0天，所以要減1
//                d = d - 1;
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(firstDate);
//                calendar.add(Calendar.DATE, d);
//                memoryDay.date = sdf.format(calendar.getTime().getTime());
//                memoryDays.add(memoryDay);
//            }

            //周年紀念日
            for (int i = 1; i <= 100; i++) {
                Calendar c = Calendar.getInstance();
                c.setTime(firstDate);
                c.add(Calendar.YEAR, i);
                MemoryDay anniversary = new MemoryDay();
                anniversary.date = sdf.format(c.getTime());
                anniversary.mark = i + "周年";
                anniversary.anniversary = true;
                anniversary.days = (int) ((c.getTimeInMillis() - firstDate.getTime()) / ((1000 * 60 * 60 * 24)));
                memoryDays.add(anniversary);
            }

            Collections.sort(memoryDays, new Comparator<MemoryDay>() {
                @Override
                public int compare(MemoryDay obj1, MemoryDay obj2) {
                    return Integer.valueOf(obj1.days).compareTo(Integer.valueOf(obj2.days)); // To compare integer values
                }
            });

            for (MemoryDay md : memoryDays) {
                db.memoryDayDao().insert(md);
//                setAlarmManager(md);
            }
        }

        ArrayList<MemoryDay> objects = (ArrayList<MemoryDay>) db.memoryDayDao().getAll();
        Collections.sort(objects, new Comparator<MemoryDay>() {
            @Override
            public int compare(MemoryDay obj1, MemoryDay obj2) {
                return Integer.valueOf(obj1.days).compareTo(Integer.valueOf(obj2.days)); // To compare integer values
            }
        });
        MemoryDayAdapter memoryDayMemoryDayAdapter = new MemoryDayAdapter(getContext(), R.layout.memory_day_before_firstdate, objects);

        listView.setAdapter(memoryDayMemoryDayAdapter);
    }


    private void addMemoryDay(String mark, String date, Date newMemoryDate, Date firstDate) {
        MemoryDay memoryDay = new MemoryDay();
        memoryDay.date = date;
        memoryDay.mark = mark;
        memoryDay.anniversary = false;
        memoryDay.days = (int) ((newMemoryDate.getTime() - firstDate.getTime()) / ((1000 * 60 * 60 * 24)));
        db.memoryDayDao().insert(memoryDay);
//        setAlarmManager(memoryDay);
    }

//    private void setAlarmManager(MemoryDay md) {
//        Intent intent = new Intent(getContext(), AlertReceiver.class);
//        intent.putExtra("content", "你們已經在一起 " + md.days + "天");
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//        Calendar calendar = Calendar.getInstance();
//        try {
//            calendar.setTime(sdf.parse(md.date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        //固定紀念日8點通知
//        calendar.add(Calendar.HOUR, 8);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//    }
}
