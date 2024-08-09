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

import com.example.item.ItemHomeContent;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ItemHomeContent> dataList;
    private final Context mContext;
    private RvOnClickListener clickListener;
    private final int columnWidth;
    private final boolean isHomeMore;
    private final int VIEW_TYPE_MOVIE = 0;
    private final int VIEW_TYPE_SHOW = 1; // sport and tv also have same layout so

    public HomeContentAdapter(Context context, ArrayList<ItemHomeContent> dataList, boolean isHomeMore) {
        this.dataList = dataList;
        this.mContext = context;
        columnWidth = MyApplication.getInstance().getScreenWidth();
        this.isHomeMore = isHomeMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_MOVIE:
            default:
                View vMovie = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_movie_item, parent, false);
                return new MovieItemRowHolder(vMovie);
            case VIEW_TYPE_SHOW:
                View vShow = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_show_item, parent, false);
                return new ShowItemRowHolder(vShow);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_MOVIE) {
            final MovieItemRowHolder holder = (MovieItemRowHolder) viewHolder;
            final ItemHomeContent singleItem = dataList.get(position);
            holder.image.setLayoutParams(new RelativeLayout.LayoutParams((int) (columnWidth / 3.2), columnWidth / 3 + 80));

            if (!isHomeMore) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (columnWidth / 3.2), ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.item_space), (int) mContext.getResources().getDimension(R.dimen.item_space));
                holder.rootLayout.setLayoutParams(layoutParams);
            }
            if (!singleItem.getVideoImage().isEmpty()) {
                Picasso.get().load(singleItem.getVideoImage()).placeholder(R.drawable.place_holder_movie).into(holder.image);
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopUpAds.showInterstitialAds(mContext, holder.getBindingAdapterPosition(), clickListener);
                }
            });

            holder.ivPremium.setVisibility(singleItem.isPremium() ? View.VISIBLE : View.GONE);

        } else if (viewHolder.getItemViewType() == VIEW_TYPE_SHOW) {
            final ShowItemRowHolder holder = (ShowItemRowHolder) viewHolder;
            final ItemHomeContent singleItem = dataList.get(position);

            holder.image.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth / 2, (int) (columnWidth / 3.2)));

            if (!isHomeMore) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(columnWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.item_space), (int) mContext.getResources().getDimension(R.dimen.item_space));
                holder.rootLayout.setLayoutParams(layoutParams);
            }
            if (!singleItem.getVideoImage().isEmpty()) {
                Picasso.get().load(singleItem.getVideoImage()).placeholder(R.drawable.place_holder_show).into(holder.image);
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopUpAds.showInterstitialAds(mContext, holder.getBindingAdapterPosition(), clickListener);
                }
            });
            holder.ivPremium.setVisibility(singleItem.isPremium() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        switch (dataList.get(position).getHomeType()) {
            case "Movie":
            default:
                return VIEW_TYPE_MOVIE;
            case "Shows":
            case "Sports":
            case "LiveTV":
            case "Recent":
                return VIEW_TYPE_SHOW;
        }
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class MovieItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, ivPremium;
        CardView cardView;
        RelativeLayout rootLayout;

        MovieItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            ivPremium = itemView.findViewById(R.id.ivPremium);
            cardView = itemView.findViewById(R.id.cardView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

    static class ShowItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, ivPremium;
        CardView cardView;
        RelativeLayout rootLayout;

        ShowItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            ivPremium = itemView.findViewById(R.id.ivPremium);
            cardView = itemView.findViewById(R.id.cardView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
