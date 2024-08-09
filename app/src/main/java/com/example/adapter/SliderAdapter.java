package com.example.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.item.ItemSlider;
import com.example.util.EnchantedViewPager;
import com.example.videostreamingapp.MovieDetailsActivity;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.ShowDetailsActivity;
import com.example.videostreamingapp.SportDetailsActivity;
import com.example.videostreamingapp.TVDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    private final LayoutInflater inflater;
    private final Activity context;
    private final ArrayList<ItemSlider> mList;

    public SliderAdapter(Activity context, ArrayList<ItemSlider> itemChannels) {
        this.context = context;
        this.mList = itemChannels;
        inflater = context.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = inflater.inflate(R.layout.row_slider_item, container, false);
        assert imageLayout != null;
        ImageView imageView = imageLayout.findViewById(R.id.image);
        ImageView ivPremium = imageLayout.findViewById(R.id.ivPremium);
//        TextView textTitle = imageLayout.findViewById(R.id.text);
        RelativeLayout rootLayout = imageLayout.findViewById(R.id.rootLayout);

//        textTitle.setSelected(true);

        final ItemSlider itemChannel = mList.get(position);
        if (!itemChannel.getSliderImage().isEmpty()) {
            Picasso.get().load(itemChannel.getSliderImage()).placeholder(R.drawable.place_holder_show).into(imageView);
        }
//        textTitle.setText(itemChannel.getSliderTitle());
        rootLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> aClass;
                String recentId = itemChannel.getId();
                String recentType = itemChannel.getSliderType();
                switch (recentType) {
                    case "Movies":
                        aClass = MovieDetailsActivity.class;
                        break;
                    case "Shows":
                        aClass = ShowDetailsActivity.class;
                        break;
                    case "LiveTV":
                        aClass = TVDetailsActivity.class;
                        break;
                    default:
                        aClass = SportDetailsActivity.class;
                        break;
                }
                Intent intent = new Intent(context, aClass);
                intent.putExtra("Id", recentId);
                context.startActivity(intent);
            }
        });

        ivPremium.setVisibility(itemChannel.isPremium() ? View.VISIBLE : View.GONE);
        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
