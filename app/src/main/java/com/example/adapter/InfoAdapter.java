package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videostreamingapp.R;
import com.google.android.material.imageview.ShapeableImageView;


import java.util.ArrayList;

public class InfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<String> dataList;

    public InfoAdapter(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_info_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;

        holder.ivDot.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.tvInfoName.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }


    static class ItemRowHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivDot;
        TextView tvInfoName;

        ItemRowHolder(View itemView) {
            super(itemView);
            ivDot = itemView.findViewById(R.id.ivDot);
            tvInfoName = itemView.findViewById(R.id.tvInfoName);
        }
    }
}
