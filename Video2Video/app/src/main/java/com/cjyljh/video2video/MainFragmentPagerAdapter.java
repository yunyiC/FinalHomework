package com.cjyljh.video2video;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author Administrator
 * @date 2019/7/20
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mListFragments = null;

    public MainFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> listFragments) {
        super(fragmentManager);
        mListFragments = listFragments;
    }

    @Override
    public int getCount() {
        return mListFragments.size();
    }

    @Override
    public Fragment getItem(int i) {
        return mListFragments.get(i);
    }
}
