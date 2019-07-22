package com.cjyljh.video2video;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cjyljh.video2video.fragments.FragmentVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjiayao
 * @date 2019/7/20
 */
public class AdapterRecyclerView extends RecyclerView.Adapter<MainViewHolder> {

    private List<FragmentVideo> mListFragments = new ArrayList<>();

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建View和对应的ViewHolder
        FragmentVideo fragmentVideoT = new FragmentVideo();
        mListFragments.add(fragmentVideoT);
        View view = fragmentVideoT.getView();
        MainViewHolder viewHolder = new MainViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder viewHolder, int position) {
        //将数据绑定到对应的View
        FragmentVideo fragmentVideoT = mListFragments.get(position);
        TextView textView = fragmentVideoT.getView().findViewById(R.id.tv_waitting);
        textView.setText("666");
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
