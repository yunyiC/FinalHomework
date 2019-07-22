package com.cjyljh.video2video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.airbnb.lottie.LottieAnimationView;
import com.cjyljh.video2video.fragments.FragmentGet;
import com.cjyljh.video2video.fragments.FragmentMe;
import com.cjyljh.video2video.fragments.FragmentSet;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chenjiayao
 * @date 2019/7/19
 *
 */
public class MainActivity extends AppCompatActivity {

    public static MainActivity main;

    private static final String TAG = MainActivity.class.getSimpleName();

    private View mRootView;
    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private int[] mItemIds = new int[] {
            R.id.nav_item_get,
            R.id.nav_item_set,
            R.id.nav_item_me,
    };
    private TabListener mTabListener;
    private MainFragmentPagerAdapter mAdapter;
    private FragmentGet mFragmentGet;
    private FragmentSet mFragmentSet;
    public FragmentMe mFragmentMe;
    private List<Fragment> mListFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = this;

        mRootView = findViewById(R.id.view_main);

        mViewPager = findViewById(R.id.vp_main);
        mViewPager.setOffscreenPageLimit(3);
        mBottomNavigationView = findViewById(R.id.bnv_main);

        mTabListener = new TabListener();
        mViewPager.addOnPageChangeListener(mTabListener);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mTabListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragmentGet = new FragmentGet();
        mFragmentSet = new FragmentSet();
        mFragmentMe = new FragmentMe();
        mListFragments = new ArrayList<>();
        mListFragments.add(mFragmentGet);
        mListFragments.add(mFragmentSet);
        mListFragments.add(mFragmentMe);
        mAdapter = new MainFragmentPagerAdapter(fragmentManager, mListFragments);
        mViewPager.setAdapter(mAdapter);

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,},
                PackageManager.PERMISSION_GRANTED);

        mRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: ");
                LottieAnimationView lavLoading = mRootView.findViewById(R.id.loading);

                if (lavLoading != null) {
                    lavLoading.startAnimation(AnimationUtils.loadAnimation(mRootView.getContext(), R.anim.fade_out));
                }

                mRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (lavLoading != null) {
                            lavLoading.setVisibility(View.INVISIBLE);
                        }
                    }
                }, getResources().getInteger(R.integer.time_anim_play_fade));
            }
        }, getResources().getInteger(R.integer.time_loading));

    }

    private class TabListener
    implements ViewPager.OnPageChangeListener , BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            mBottomNavigationView.setSelectedItemId(mItemIds[i]);
            switch(i) {
                case 0:
                    mFragmentSet.closeCamera();
                    mFragmentGet.refresh();
                    break;
                case 1:
                    mFragmentSet.openCamera();
                    break;
                case 2:
                    mFragmentSet.closeCamera();
                    break;
                    default:
                        break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int idT = menuItem.getItemId();
            for(int i=0;i<mItemIds.length;i++) {
                if (idT == mItemIds[i]) {
                    mViewPager.setCurrentItem(i, true);
                    return true;
                }
            }
            return false;
        }
    }

    public static void startPlayerActivity(String uriVideo) {
        Intent intent = new Intent(main.getBaseContext(), PlayerActivity.class);
        intent.putExtra("uriVideo", uriVideo);
        main.startActivity(intent);
    }
}
