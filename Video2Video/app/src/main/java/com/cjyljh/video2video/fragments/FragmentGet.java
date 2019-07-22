package com.cjyljh.video2video.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjyljh.video2video.MainActivity;
import com.cjyljh.video2video.R;
import com.cjyljh.video2video.bean.Feed;
import com.cjyljh.video2video.network.NetworkOperate;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Administrator
 * @date 2019/7/20
 */
public class FragmentGet extends Fragment {
    private static final String TAG = FragmentGet.class.getSimpleName();

    private View mRootView = null;
    private ListView mLvVideo;
//    private RecyclerView mRvVideo;
//    private VideoListAdapter mAdapter;
    private List<View> mListViews;
    private List<Feed> mFeeds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_get, container, false);
            mLvVideo = mRootView.findViewById(R.id.lv_video);
            /////////////////////////////////////////
//            mRvVideo = mRootView.findViewById(R.id.rv_video);
//            mRvVideo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//            mRvVideo.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
//            mAdapter = new VideoListAdapter(new VideoOperator() {
//                @Override
//                public void doNothing() {
//
//                }
//            });
//            mRvVideo.setAdapter(mAdapter);
            /////////////////////////

            mListViews = new ArrayList<>();
            mListViews.add(new FragmentVideo().getView());
            mListViews.add(new FragmentVideo().getView());
            mListViews.add(new FragmentVideo().getView());

            refresh();
        }

        return mRootView;
    }

    private class AdapterVideoShow extends BaseAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            return mListViews.get(position % mListViews.size());
        }

        @Override
        public long getItemId(int position) {
            return position % mListViews.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_video, null);
            }

            TextView tvWaitting = convertView.findViewById(R.id.tv_waitting);
            ImageView ivVideo = convertView.findViewById(R.id.lv_video);
            if (0 <= position && position < mFeeds.size()) {
                tvWaitting.setVisibility(View.INVISIBLE);
                Feed feed = mFeeds.get(position);
                String uri = feed.getImageUrl();
                Glide.with(ivVideo.getContext()).load(uri).into(ivVideo);
                convertView.setTag(feed);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 开启播放器
                        Feed feed = (Feed)v.getTag();
                        MainActivity.startPlayerActivity(feed.getVideoUrl());
                    }
                });
            } else {
                tvWaitting.setVisibility(View.VISIBLE);
                tvWaitting.setText(getResources().getString(R.string.load_fail));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "重新加载", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return convertView;
        }
    }

    public void setFeeds(List<Feed> feeds){
        mFeeds = feeds;
        mLvVideo.setAdapter(new AdapterVideoShow());
//        mAdapter.refresh(mFeeds);
    }

    public void refresh() {
        NetworkOperate.startDownloadFeed(this);
    }
}
