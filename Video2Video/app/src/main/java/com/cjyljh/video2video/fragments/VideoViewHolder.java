package com.cjyljh.video2video.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjyljh.video2video.MainActivity;
import com.cjyljh.video2video.R;
import com.cjyljh.video2video.bean.Feed;

/**
 * @author Administrator
 * @date 2019/7/21
 */
public class VideoViewHolder extends RecyclerView.ViewHolder {
    private final VideoOperator operator;

    private TextView mTvWaitting;
    private ImageView mIvVideo;

    public VideoViewHolder(@NonNull View itemView, VideoOperator operator) {
        super(itemView);
        this.operator = operator;

        mTvWaitting = itemView.findViewById(R.id.tv_waitting);
        mIvVideo = itemView.findViewById(R.id.lv_video);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 开启播放器
                Feed feed = (Feed)v.getTag();
                MainActivity.startPlayerActivity(feed.getVideoUrl());
            }
        });

    }

    public void bind(final Feed feed) {
        if (feed == null) {
            mTvWaitting.setVisibility(View.VISIBLE);
            mTvWaitting.setText(MainActivity.main.getResources().getString(R.string.load_fail));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.main.getBaseContext(), "重新加载", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mTvWaitting.setVisibility(View.INVISIBLE);
            String uri = feed.getImageUrl();
            Glide.with(mIvVideo.getContext()).load(uri).into(mIvVideo);
            itemView.setTag(feed);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 开启播放器
                    Feed feed = (Feed)v.getTag();
                    MainActivity.startPlayerActivity(feed.getVideoUrl());
                }
            });
        }
    }
}
