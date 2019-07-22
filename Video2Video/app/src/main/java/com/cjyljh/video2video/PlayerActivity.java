package com.cjyljh.video2video;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

/**
 *
 * @author chenjiayao
 * @date 2019/07/18
 *
 */
public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = PlayerActivity.class.getSimpleName();

    private static final int UPDATE = 0;

    public static boolean isBackUp = false;
    public static boolean isPause = false;
    public static int currentPosition = 0;

    private Handler handler;

    private VideoView mVvPlayer;
    private ImageView mIvBtnPlayPause;
    private SeekBar mSbPlayer;
    private TextView mTvInfo;

    private PlayPauseListener mListenerPlayPause;
    private Drawable mImgPlay;
    private Drawable mImgPause;

    private static final int PROGRESS_MAX = 100;
    private static final int TIME_UPDATE = 300;
    private int timeNoTouch = 0;
    private static final int TIME_FADE = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        init();
    }

    private void init() {
        handler = new SeekBarHandler();

        mVvPlayer = findViewById(R.id.vv_player);
        mIvBtnPlayPause = findViewById(R.id.iv_button_play_pause);
        mSbPlayer = findViewById(R.id.sb_player);
        mImgPlay = getResources().getDrawable(R.drawable.play, getTheme());
        mImgPause = getResources().getDrawable(R.drawable.pause, getTheme());
        mTvInfo = findViewById(R.id.tv_info);

        mListenerPlayPause = new PlayPauseListener();
        mIvBtnPlayPause.setOnClickListener(mListenerPlayPause);
        mVvPlayer.setOnClickListener(mListenerPlayPause);
        String strUriVideo = getIntent().getStringExtra("uriVideo");
        Uri uri = Uri.parse(strUriVideo);
        mVvPlayer.setVideoURI(uri);
        mVvPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: 完成播放");
                pause();
            }
        });

        mSbPlayer.setMax(PROGRESS_MAX);
        mSbPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int progressT = seekBar.getProgress() * mVvPlayer.getDuration() / PROGRESS_MAX;
                    mVvPlayer.seekTo(progressT);
                    timeNoTouch = 0;
                    Log.d(TAG, "onProgressChanged: 改变进度条");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: 进入进度条");
                pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: 离开进度条");
                play();
            }
        });
        mIvBtnPlayPause.setBackground(mImgPause);
        new SeekBarThread().start();
        mVvPlayer.start();

        if (isBackUp) {
            isBackUp = false;

            mVvPlayer.seekTo(currentPosition);

            if (isPause) {
                pause();
            } else {
                play();
            }
        }

        Log.d(TAG, "init: 初始化工作全部完成");

    }

    private void pause() {
        mVvPlayer.pause();
        mIvBtnPlayPause.setBackground(mImgPlay);
        timeNoTouch = 0;
        mIvBtnPlayPause.setAlpha(1.0f);
        backUp();
    }

    private void play() {
        mVvPlayer.start();
        mIvBtnPlayPause.setBackground(mImgPause);
        timeNoTouch = 0;
        mIvBtnPlayPause.setAlpha(1.0f);
        backUp();
    }

    private void backUp() {
        isBackUp = true;
        isPause = !mVvPlayer.isPlaying();
        currentPosition = mVvPlayer.getCurrentPosition();

        refreshTimeAndProgress();
    }

    private void refreshTimeAndProgress() {
        long time = mVvPlayer.getCurrentPosition();
        int hour = (int)(time / (60 * 60 * 1000));
        int minute = (int)(time / (60 * 1000));
        int second = (int)(time / 1000);
        float progress = 100.0f * mVvPlayer.getCurrentPosition() / mVvPlayer.getDuration();
        String strR = String.format("%d:%d:%d   %.2f %%", hour, minute, second, progress);
        mTvInfo.setText(strR);
        Log.d(TAG, "refreshTimeAndProgress: strR = " + strR);
    }

    private class PlayPauseListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mVvPlayer.isPlaying()) {
                Log.d(TAG, "onClick: 暂停");
                pause();
            } else {
                Log.d(TAG, "onClick: 继续");
                play();
            }
        }
    }

    private class SeekBarHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE) {
                if (mVvPlayer.isPlaying()) {
                    backUp();
                    int progress = PROGRESS_MAX * mVvPlayer.getCurrentPosition() / mVvPlayer.getDuration();
                    mSbPlayer.setProgress(progress);
                    Log.d(TAG, "handleMessage: 更新成功" + mVvPlayer.getCurrentPosition());

                    timeNoTouch += TIME_UPDATE;
                    Log.d(TAG, "handleMessage: TimeNoTouch=" + timeNoTouch);
                    if (timeNoTouch < TIME_FADE) {
                        mIvBtnPlayPause.setAlpha(1.0f);
                    }else{
                        mIvBtnPlayPause.setAlpha(0.0f);
                    }
                }
            }
        }
    }

    /**
     * 更新seekBar
     */
    private class SeekBarThread extends Thread {
        @Override
        public void run() {
            //Log.d(TAG, "run: 更新中......");
            Message msg = new Message();
            msg.what = UPDATE;
            handler.sendMessage(msg);
            //0.5s后再次执行
            try {
                sleep(TIME_UPDATE);
                new SeekBarThread().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
