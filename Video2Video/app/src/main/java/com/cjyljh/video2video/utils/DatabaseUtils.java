package com.cjyljh.video2video.utils;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cjyljh.video2video.History;
import com.cjyljh.video2video.MainActivity;
import com.cjyljh.video2video.User;
import com.cjyljh.video2video.database.DataBaseHelper;
import com.cjyljh.video2video.database.Identity;
import com.cjyljh.video2video.database.UserContract;
import com.cjyljh.video2video.database.UserInfo;
import com.cjyljh.video2video.database.VideoContract;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {

    private static DataBaseHelper mDataBaseHelper = null;
    private static SQLiteDatabase mSqLiteDatabase = null;

    private static DataBaseHelper getDataBaseHelper() {
        if (mDataBaseHelper == null) {
            mDataBaseHelper = new DataBaseHelper(MainActivity.main.getBaseContext());
        }

        return mDataBaseHelper;
    }

    private static SQLiteDatabase getSqLiteDatabase() {
        if (mSqLiteDatabase == null) {
            mSqLiteDatabase = getDataBaseHelper().getWritableDatabase();
        }

        return mSqLiteDatabase;
    }

    public static User getUser() {

        User user = new User();

        //==================获取用户名和身份=========================//
        String[] users = {
                UserContract.UserTable.USER_NAME,
                UserContract.UserTable.IDENTITY
        };

        Cursor cursor = getSqLiteDatabase().query(
                UserContract.UserTable.TABLE_NAME,
                users,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            try{
                String strUsername = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserTable.USER_NAME));
                int identity = cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserTable.IDENTITY));
                user.username = strUsername;
                user.identity = Identity.from(identity);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (user.username == null) {
            UserInfo userInfo = new UserInfo(1009, "用户008", "123", Identity.VIP);
            insertUserInfo(userInfo);
            return getUser();
        }

        //获取历史发布记录=======================================//
        String[] videos = {
                VideoContract.VideoTable.COVER_IMAGE_URI,
                VideoContract.VideoTable.VIDEO_URI,
                VideoContract.VideoTable.PLAY_TIME,
                VideoContract.VideoTable.THUMBS_UP_TIMES,
                VideoContract.VideoTable.UPLOADER ,
        };

        cursor = getSqLiteDatabase().query(
                VideoContract.VideoTable.TABLE_NAME,
                videos,
                null,
                null,
                null,
                null,
                null
        );

        List<History> listHistory = new ArrayList<>();
        while (cursor.moveToNext()) {
            try{
                History history = new History();
                history.uriImage = cursor.getString(cursor.getColumnIndexOrThrow(VideoContract.VideoTable.COVER_IMAGE_URI));
                history.uriVideo = cursor.getString(cursor.getColumnIndexOrThrow(VideoContract.VideoTable.VIDEO_URI));
                history.timePlay = cursor.getInt(cursor.getColumnIndexOrThrow(VideoContract.VideoTable.PLAY_TIME));
                history.timeUp = cursor.getInt(cursor.getColumnIndexOrThrow(VideoContract.VideoTable.THUMBS_UP_TIMES));
                history.uploader = cursor.getString(cursor.getColumnIndexOrThrow(VideoContract.VideoTable.UPLOADER));

                Log.d("test_image", "uriImage = [" + history.uriImage + "]");

                listHistory.add(history);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        user.listHistory = listHistory;
        return  user;
    }

    public static void insertHistory(History history) {
        ContentValues valuesT = new ContentValues();
        valuesT.put(VideoContract.VideoTable.VIDEO_TITLE, "title");
        valuesT.put(VideoContract.VideoTable.COVER_IMAGE_URI, history.uriImage);
        valuesT.put(VideoContract.VideoTable.VIDEO_URI, history.uriVideo);
        valuesT.put(VideoContract.VideoTable.PLAY_TIME, history.timePlay);
        valuesT.put(VideoContract.VideoTable.THUMBS_UP_TIMES, history.timeUp);
        valuesT.put(VideoContract.VideoTable.UPLOADER, history.uploader);

        getSqLiteDatabase().insert(VideoContract.VideoTable.TABLE_NAME,
                null, valuesT);

    }

    public static void insertUserInfo(UserInfo userInfo) {
        ContentValues valuesT = new ContentValues();
        valuesT.put(UserContract.UserTable.USER_NAME, userInfo.getUserName());
        valuesT.put(UserContract.UserTable.PASSWORD, userInfo.getPassword());
        valuesT.put(UserContract.UserTable.IDENTITY, userInfo.getIdentity().intValue);

        getSqLiteDatabase().insert(UserContract.UserTable.TABLE_NAME,
                null, valuesT);

    }


}
