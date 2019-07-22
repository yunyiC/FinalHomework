package com.cjyljh.video2video.database;

public class UserInfo {
    private long userId;
    private String userName;
    private String password;
    private Identity identity;

    public UserInfo(long userId, String userName, String password, Identity identity){
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.identity = identity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
