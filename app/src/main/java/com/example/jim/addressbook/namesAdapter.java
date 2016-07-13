package com.example.jim.addressbook;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class namesAdapter extends BaseAdapter {
    ArrayList<names> arr;
    Context ctx;

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(ctx, R.layout.activity_name, null);

        TextView name = (TextView)convertView.findViewById(R.id.tv_names);

        names n = arr.get(position);

        name.setText(n.name);


        return convertView;
    }
}
