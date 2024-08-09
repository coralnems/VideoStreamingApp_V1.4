package com.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.item.ItemSeason;
import com.example.videostreamingapp.R;

import java.util.ArrayList;

public class SeasonAdapter extends ArrayAdapter<ItemSeason> {

    private final ArrayList<ItemSeason> dataList;
    private final Context mContext;

    public SeasonAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ItemSeason> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.dataList = objects;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Nullable
    @Override
    public ItemSeason getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(mContext.getResources().getColor(R.color.text));
        label.setText(dataList.get(position).getSeasonName());
        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(dataList.get(position).getSeasonName());
        return label;
    }
}
