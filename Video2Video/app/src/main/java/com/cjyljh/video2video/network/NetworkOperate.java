package com.cjyljh.video2video.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.cjyljh.video2video.bean.Feed;
import com.cjyljh.video2video.bean.FeedResponse;
import com.cjyljh.video2video.bean.PostVideoResponse;
import com.cjyljh.video2video.fragments.FragmentGet;
import com.cjyljh.video2video.utils.IMiniDouyinService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.internal.EverythingIsNonNull;

public class NetworkOperate {

    private static NetworkOperate main = new NetworkOperate();

    private static final String TAG = "NetworkOperate";

    private static FragmentGet target;

    private static List<Feed> feeds = new ArrayList<>();
    private static List<Call> mCallList = new ArrayList<>();
    private static Context context = null;

    private NetworkOperate() {
    }

    public static void startDownloadFeed(FragmentGet fragment) {
        target = fragment;
        context = fragment.getContext();
        fetchFeed();
    }

    public void postVideo(String id, String userName, Uri ImageUri, Uri videoUri) {
        MultipartBody.Part image = getMultipartFromUri("cover_image", ImageUri);
        MultipartBody.Part video = getMultipartFromUri("video", videoUri);

        Call<PostVideoResponse> postVideoResponseCall = getPostVideoResponseWithRetrofitAsync(id, userName, image, video);
        mCallList.add(postVideoResponseCall);

        postVideoResponseCall.enqueue(new Callback<PostVideoResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
                }
                mCallList.remove(call);
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                Toast.makeText(context, "网络连接出现问题", Toast.LENGTH_SHORT).show();
                mCallList.remove(call);
            }
        });
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(context, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private static void fetchFeed() {
        Call<FeedResponse> feedResponseCall = getFeedResponseWithRetrofitAsync();
        mCallList.add(feedResponseCall);
        feedResponseCall.enqueue(new Callback<FeedResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        feeds = response.body().getFeeds();
                        target.setFeeds(feeds);
                    }
                }
                else{
                    Log.d(TAG, "onResponse: 获取JSON文件失败");
                }
                mCallList.remove(call);
            }
            @EverythingIsNonNull
            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Toast.makeText(context, "请检查网络链接", Toast.LENGTH_SHORT).show();
                mCallList.remove(call);
            }
        });
    }

    public static List<Feed> getFeeds() {
        return feeds;
    }

    private static Call<FeedResponse> getFeedResponseWithRetrofitAsync(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IMiniDouyinService.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(IMiniDouyinService.class).fetchFeed();
    }

    private static Call<PostVideoResponse> getPostVideoResponseWithRetrofitAsync(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video
    ) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IMiniDouyinService.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(IMiniDouyinService.class).createVideo(studentId, userName, image, video);
    }
}
