package com.cjyljh.video2video.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjyljh.video2video.R;

/**
 * @author Administrator
 * @date 2019/7/20
 */
public class FragmentVideo extends Fragment {
    private static final String TAG = FragmentSet.class.getSimpleName();

    public View viewRoot = null;

    private TextView mTvWaitting;
    private ImageView mIvVideo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewRoot == null) {
            viewRoot = inflater.inflate(R.layout.item_video, container, false);
            mTvWaitting = viewRoot.findViewById(R.id.tv_waitting);
            mIvVideo = viewRoot.findViewById(R.id.lv_video);
        }

        return viewRoot;
    }
}
