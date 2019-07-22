package com.cjyljh.video2video;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author Administrator
 * @date 2019/7/19
 */
public class AdapterViewPager extends PagerAdapter {

    private List<View> mListView;

    public AdapterViewPager(List<View> listView) {
        this.mListView = listView;
    }

    @Override
    public int getCount() {
        return mListView.size();
    }

    /**
     * 判断视图和Key是否对应
     * 这里可以直接判等
     *
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * 移除指定位置的视图
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView(mListView.get(position));
    }

    /**
     * 将给定位置的视图添加到显示区
     * 返回该视图的Key(一般是视图本身)
     *
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //super.instantiateItem(container, position);
        container.addView(mListView.get(position));
        return mListView.get(position);
    }
}
