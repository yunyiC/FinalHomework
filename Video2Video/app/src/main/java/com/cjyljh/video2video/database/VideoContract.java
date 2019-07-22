package com.cjyljh.video2video.database;

import android.provider.BaseColumns;

public class VideoContract {
    public static class VideoTable implements BaseColumns {
        public static final String TABLE_NAME = "Video";
        public static final String VIDEO_TITLE = "Title";
        public static final String COVER_IMAGE_URI = "CoverImageUri";
        public static final String VIDEO_URI = "VideoUri";
        public static final String PLAY_TIME = "PlayTime";
        public static final String THUMBS_UP_TIMES = "ThumbsUpTimes";
        public static final String UPLOADER = "Uploader";
    }

    private static final String SQL_CREATE_VIDEO_TABLE =
            "CREATE TABLE " + VideoTable.TABLE_NAME + " (" +
                    VideoTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            VideoTable.VIDEO_TITLE + " TEXT, " +
            VideoTable.COVER_IMAGE_URI + " TEXT, " +
            VideoTable.VIDEO_URI + " TEXT, " +
            VideoTable.PLAY_TIME + " INTEGER, " +
            VideoTable.THUMBS_UP_TIMES + " INTEGER, " +
            VideoTable.UPLOADER + " TEXT)";

    private static final String SQL_DELETE_VIDEO_TABLE =
            "DROP TABLE IF EXISTS " + VideoTable.TABLE_NAME;

    public static String getSqlCreateVideoTable() {
        return SQL_CREATE_VIDEO_TABLE;
    }

    public static String getSqlDeleteVideoTable() {
        return SQL_DELETE_VIDEO_TABLE;
    }
}
