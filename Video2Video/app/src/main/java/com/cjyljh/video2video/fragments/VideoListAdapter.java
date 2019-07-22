package com.cjyljh.video2video.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjyljh.video2video.R;
import com.cjyljh.video2video.bean.Feed;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2019/7/21
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoViewHolder> {
    private final VideoOperator operator;
    private final List<Feed> feeds = new ArrayList<>();

    public VideoListAdapter(VideoOperator operator) {
        this.operator = operator;
    }

    public void refresh(List<Feed> newNotes) {
        feeds.clear();
        if (newNotes != null) {
            feeds.addAll(newNotes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(itemView, operator);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int pos) {
        holder.bind(feeds.get(pos));
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }
}
