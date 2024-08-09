package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemPlan;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;

import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ItemPlan> dataList;
    private final Context mContext;
    private RvOnClickListener clickListener;
    private int row_index = -1;

    public PlanAdapter(Context context, ArrayList<ItemPlan> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_plan, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemPlan singleItem = dataList.get(position);
        holder.textPlanName.setText(singleItem.getPlanName());
        holder.textPlanPrice.setText(singleItem.getPlanPrice());
        holder.textPlanCurrency.setText(singleItem.getPlanCurrencyCode());
        holder.textPlanDuration.setText(mContext.getString(R.string.plan_day_for, singleItem.getPlanDuration()));
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
                holder.rootLayout.setCardBackgroundColor(mContext.getResources().getColor(R.color.plan_select));
                holder.radioButton.setChecked(true);
            } else {
                holder.rootLayout.setCardBackgroundColor(mContext.getResources().getColor(R.color.plan_normal));
                holder.radioButton.setChecked(false);
            }

        }

    }

    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView textPlanName;
        TextView textPlanPrice;
        TextView textPlanDuration;
        TextView textPlanCurrency;
        RadioButton radioButton;
        CardView rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            textPlanName = itemView.findViewById(R.id.textPackName);
            textPlanPrice = itemView.findViewById(R.id.textPrice);
            textPlanDuration = itemView.findViewById(R.id.textDay);
            textPlanCurrency = itemView.findViewById(R.id.textCurrency);
            radioButton = itemView.findViewById(R.id.radioButton);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

}
