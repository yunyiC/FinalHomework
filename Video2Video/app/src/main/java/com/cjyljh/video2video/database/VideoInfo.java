package com.cjyljh.video2video.database;

public class VideoInfo {
    private long videoId;
    private String videoTitle;
    private String coverImageUri;
    private String videoUri;
    private int playTime;
    private int thumbsUpTimes;
    private String uploader;

    public VideoInfo(
            long videoId,
            String videoTitle,
            String coverImageUri,
            String videoUri,
            int playTime,
            int thumbsUpTimes,
            String uploader
    ) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.coverImageUri = coverImageUri;
        this.videoUri = videoUri;
        this.playTime = playTime;
        this.thumbsUpTimes = thumbsUpTimes;
        this.uploader = uploader;
    }

    public long getVideoId() {
        return videoId;
    }

    public int getPlayTime() {
        return playTime;
    }

    public int getThumbsUpTimes() {
        return thumbsUpTimes;
    }

    public String getCoverImageUri() {
        return coverImageUri;
    }

    public String getUploader() {
        return uploader;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoUri() {
        return videoUri;
    }

}
