package com.example.a20n9.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a20n9.R;
import com.example.a20n9.database.entity.MemoryDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.recyclerview.widget.RecyclerView;

public class MemoryDayAdapter extends ArrayAdapter<MemoryDay> {


    ArrayList<MemoryDay> ls;
    Context mContext;
    int mResource;

    final int VIEW_TYPE = 3;
    final int BEFORE_FIRSTDATE = 0;
    final int EQUAL_FIRSTDATE = 1;
    final int AFTER_FIRSTDATE = 2;

    public MemoryDayAdapter(@NonNull Context context, int resource, ArrayList<MemoryDay> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.ls = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MemoryDay memoryDay = ls.get(position);
        if (convertView == null) {
            if (getItemViewType(position) == BEFORE_FIRSTDATE) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.memory_day_before_firstdate, parent, false);
            } else if (getItemViewType(position) == EQUAL_FIRSTDATE) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.memory_day_equal_firstdate, parent, false);
            } else if (getItemViewType(position) == AFTER_FIRSTDATE) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.memory_day_after_firstdate, parent, false);
            }
        }


        TextView tvMark = convertView.findViewById(R.id.tvMark);
        tvMark.setText(memoryDay.mark);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        tvDate.setText(memoryDay.date);


        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        MemoryDay memoryDay = ls.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date memoryDate = null;
        try {
            memoryDate = sdf.parse(memoryDay.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date today = Calendar.getInstance().getTime();
        try {
            today = sdf.parse(sdf.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (memoryDate.before(today)) {
            return BEFORE_FIRSTDATE;
        } else if (memoryDate.after(today)) {
            return AFTER_FIRSTDATE;
        } else {
            return EQUAL_FIRSTDATE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }
}
