package com.cjyljh.verticalviewpager;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * @author Administrator
 * @date 2019/7/20
 */
public class DefaultTransformer implements ViewPager.PageTransformer {

    public static final String TAG = "DefaultTransformer";

    @Override
    public void transformPage(View view, float position) {

        float transX = view.getWidth() * position * (-1) * 1.5f;
        view.setTranslationX(transX);
        float transY = position * view.getHeight() * 0.5f;
        view.setTranslationY(transY);

        float alpha = 0;
        if (0 <= position && position <= 1) {
            alpha = 1 - position;
        } else if (-1 < position && position < 0) {
            alpha = position + 1;
        }
        view.setAlpha(alpha);
    }
}
