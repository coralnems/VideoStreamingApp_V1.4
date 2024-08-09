package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemTV;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeTVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ItemTV> dataList;
    private final Context mContext;
    private RvOnClickListener clickListener;
    private final int columnWidth;

    public HomeTVAdapter(Context context, ArrayList<ItemTV> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        columnWidth = MyApplication.getInstance().getScreenWidth();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_sport_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemTV singleItem = dataList.get(position);
        holder.text.setText(singleItem.getTvName());
        holder.image.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth / 2, (int) (columnWidth / 3.2)));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(columnWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.item_space), (int) mContext.getResources().getDimension(R.dimen.item_space));
        holder.rootLayout.setLayoutParams(layoutParams);
        if (!singleItem.getTvImage().isEmpty()) {
            Picasso.get().load(singleItem.getTvImage()).placeholder(R.drawable.place_holder_show).into(holder.image);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getBindingAdapterPosition(), clickListener);
            }
        });

        holder.ivPremium.setVisibility(singleItem.isPremium() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, ivPremium;
        TextView text;
        CardView cardView;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            ivPremium = itemView.findViewById(R.id.ivPremium);
            cardView = itemView.findViewById(R.id.cardView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
