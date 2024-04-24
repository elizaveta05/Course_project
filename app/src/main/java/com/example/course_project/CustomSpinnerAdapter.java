package com.example.course_project;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mItems;

    public CustomSpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.WHITE); // Установите белый цвет фона

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view;
        textView.setTextColor(Color.BLACK);
        return view;
    }
}
