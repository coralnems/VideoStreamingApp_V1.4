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

import com.example.item.ItemEpisode;
import com.example.util.PopUpAds;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ItemEpisode> dataList;
    private final Context mContext;
    private RvOnClickListener clickListener;
    private int row_index = -1;
    private final boolean isPurchased;
    private final int columnWidth;

    public EpisodeAdapter(Context context, ArrayList<ItemEpisode> dataList, boolean isPurchased) {
        this.dataList = dataList;
        this.mContext = context;
        this.isPurchased = isPurchased;
        columnWidth = MyApplication.getInstance().getScreenWidth();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_episode_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemEpisode singleItem = dataList.get(position);
        holder.text.setText(singleItem.getEpisodeName());
        holder.image.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth / 2, (int) (columnWidth / 3.2)));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(columnWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.item_space), (int) mContext.getResources().getDimension(R.dimen.item_space));
        holder.rootLayout.setLayoutParams(layoutParams);
        if (!singleItem.getEpisodeImage().isEmpty()) {
            Picasso.get().load(singleItem.getEpisodeImage()).placeholder(R.drawable.place_holder_show).into(holder.image);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getBindingAdapterPosition(), clickListener);
            }
        });

        if (row_index > -1) {
            if (row_index == position) {
                holder.imagePlay.setVisibility(View.VISIBLE);
            } else {
                holder.imagePlay.setVisibility(View.GONE);
            }
        }
        holder.ivPremium.setVisibility(singleItem.isPremium() ? View.VISIBLE : View.GONE);
    /*    if (singleItem.isDownload()) {
            if (singleItem.isPremium()) {
                if (isPurchased) {
                    holder.imageDownload.setVisibility(View.VISIBLE);
                } else {
                    holder.imageDownload.setVisibility(View.GONE);
                }
            } else {
                holder.imageDownload.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imageDownload.setVisibility(View.GONE);
        }

        holder.imageDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (singleItem.getDownloadUrl().isEmpty()) {
                    Toast.makeText(mContext, mContext.getString(R.string.download_not_found), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        mContext.startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(singleItem.getDownloadUrl())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Toast.makeText(mContext, mContext.getString(R.string.invalid_download), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
     */
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, ivPremium, imagePlay;
        TextView text;
        CardView cardView;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.textEpisodes);
            ivPremium = itemView.findViewById(R.id.ivPremium);
            cardView = itemView.findViewById(R.id.cardView);
            imagePlay = itemView.findViewById(R.id.imageEpPlay);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

}
