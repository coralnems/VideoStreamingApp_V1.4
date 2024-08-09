package com.example.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.util.RippleBackground;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.R;
import com.squareup.picasso.Picasso;

public class PlayRippleFragment extends Fragment {


    public static PlayRippleFragment newInstance(String imageCover) {
        PlayRippleFragment f = new PlayRippleFragment();
        Bundle args = new Bundle();
        args.putString("imageCover", imageCover);
        f.setArguments(args);
        return f;
    }

    RvOnClickListener clickListener;
    ImageView imageCover, imagePlay;
    String imageUrl;
    RippleBackground rippleBackground;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play_ripple, container, false);

        if (getArguments() != null) {
            imageUrl = getArguments().getString("imageCover");
        }

        imageCover = rootView.findViewById(R.id.imageCover);
        imagePlay = rootView.findViewById(R.id.imagePlay);
        rippleBackground = rootView.findViewById(R.id.rippleBg);
        rippleBackground.startRippleAnimation();

        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.place_holder_show).into(imageCover);
        }

        imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(0);
            }
        });

        return rootView;
    }

    public void setOnSkipClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
