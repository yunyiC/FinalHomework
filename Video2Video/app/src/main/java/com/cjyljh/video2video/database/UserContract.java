package com.cjyljh.video2video.database;

import android.provider.BaseColumns;

public final class UserContract {



    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + UserTable.TABLE_NAME + " (" +
            UserTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            UserTable.USER_NAME + " TEXT, " +
            UserTable.PASSWORD + " TEXT, " +
            UserTable.IDENTITY + " INTEGER)";

    private static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserTable.TABLE_NAME;

    public static String getSqlCreateUserTable() {
        return SQL_CREATE_USER_TABLE;
    }

    public static String getSqlDeleteUserTable() {
        return SQL_DELETE_USER_TABLE;
    }

    public static class UserTable implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String USER_NAME = "UserName";
        public static final String PASSWORD = "Password";
        public static final String IDENTITY = "Identity";
    }
}
