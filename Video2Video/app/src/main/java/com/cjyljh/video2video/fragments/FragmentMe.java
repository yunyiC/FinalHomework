package com.cjyljh.video2video.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cjyljh.video2video.History;
import com.cjyljh.video2video.PlayerActivity;
import com.cjyljh.video2video.R;
import com.cjyljh.video2video.User;
import com.cjyljh.video2video.utils.DatabaseUtils;
/**
 * @author Administrator
 * @date 2019/7/20
 */
public class FragmentMe extends Fragment {

    public static FragmentMe main;
    private static final String TAG = FragmentMe.class.getSimpleName();

    private View mRootView = null;
    private TextView mTvUsername;
    private TextView mTvIdentity;
    private ListView mLvHistory;

    private User mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        main = this;
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_me, container, false);
            mTvUsername = mRootView.findViewById(R.id.tv_username);
            mTvIdentity = mRootView.findViewById(R.id.identity);
            mLvHistory = mRootView.findViewById(R.id.lv_hitory);

            refresh();
        }

        return mRootView;
    }

    public void refresh() {
        Handler handlerListShow = new Handler();
        Runnable getUser = new Runnable() {
            @Override
            public void run() {
                setUser(DatabaseUtils.getUser());
            }
        };
        handlerListShow.post(getUser);
    }

    private void startPlayerActivity(String uriVideo) {
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        intent.putExtra("uriVideo", uriVideo);
        startActivity(intent);
    }

    private void setUser(User user) {
        this.mUser = user;
        mTvUsername.setText(mUser.username);
        mTvIdentity.setText(mUser.identity.strValue);

        mLvHistory.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mUser.listHistory.size();
            }

            @Override
            public Object getItem(int i) {
                return mUser.listHistory.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View contentView, ViewGroup viewGroup) {
                if (contentView == null) {
                    contentView = getLayoutInflater().inflate(R.layout.item_history, null);
                }

                History history = mUser.listHistory.get(i);

                ImageView ivPreview = contentView.findViewById(R.id.iv_preview);
                TextView tvTitle = contentView.findViewById(R.id.tv_title);
                TextView tvTimePlay = contentView.findViewById(R.id.tv_time_play);
                TextView tvTimeUp = contentView.findViewById(R.id.tv_time_up);

                Glide.with(getContext()).load(history.uriImage).into(ivPreview);
                tvTitle.setText("标题:" + history.uploader);
                tvTimePlay.setText("时长:" + history.timePlay);
                tvTimeUp.setText("赞:" + history.timeUp);

                return contentView;
            }
        });

        mLvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                History history = mUser.listHistory.get(position);
                Log.d(TAG, " image_uri: " + history.uriImage + " video_uri: " + history.uriVideo);
                startPlayerActivity(history.uriVideo);
            }
        });
    }

}
