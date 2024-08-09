package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemSubTitle;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;

import java.util.ArrayList;

public class SubTitleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ItemSubTitle> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int row_index = -1;

    public SubTitleAdapter(Context context, ArrayList<ItemSubTitle> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sub_title_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemSubTitle singleItem = dataList.get(position);
        holder.textLanguageName.setText(singleItem.getSubTitleLanguage());

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(holder.getBindingAdapterPosition());
            }
        });

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(holder.getBindingAdapterPosition());
            }
        });

        if (row_index > -1) {
            if (row_index == position) {
                holder.radioButton.setChecked(true);
            } else {
                holder.radioButton.setChecked(false);
            }

        }

    }

    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    public int getSelect() {
        return row_index;
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView textLanguageName;
        RadioButton radioButton;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            textLanguageName = itemView.findViewById(R.id.text);
            radioButton = itemView.findViewById(R.id.radio);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

}
