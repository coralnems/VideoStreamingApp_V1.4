package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.item.ItemMovie;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ItemMovie> dataList;
    private final Context mContext;
    private RvOnClickListener clickListener;
    private final int columnWidth;
    private final boolean isHomeMore;

    public HomeMovieAdapter(Context context, ArrayList<ItemMovie> dataList, boolean isHomeMore) {
        this.dataList = dataList;
        this.mContext = context;
        columnWidth = MyApplication.getInstance().getScreenWidth();
        this.isHomeMore = isHomeMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_movie_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemMovie singleItem = dataList.get(position);
        holder.image.setLayoutParams(new RelativeLayout.LayoutParams((int) (columnWidth / 3.2), columnWidth / 3 + 80));

        if (!isHomeMore) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (columnWidth / 3.2), ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.item_space), (int) mContext.getResources().getDimension(R.dimen.item_space));
            holder.rootLayout.setLayoutParams(layoutParams);
        }
        if (!singleItem.getMovieImage().isEmpty()) {
            Picasso.get().load(singleItem.getMovieImage()).placeholder(R.drawable.place_holder_movie).into(holder.image);
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
        CardView cardView;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            ivPremium = itemView.findViewById(R.id.ivPremium);
            cardView = itemView.findViewById(R.id.cardView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
