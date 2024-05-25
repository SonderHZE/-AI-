package com.example.mygptapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<String> {
    public MyArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if (position == 0) {
            // 设置第一项的背景颜色
            tv.setBackgroundColor(Color.GRAY);
        } else {
            // 设置其他项的背景颜色
            tv.setBackgroundColor(Color.WHITE);
        }
        return view;
    }
}