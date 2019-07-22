package com.cjyljh.video2video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjyljh.video2video.bean.FeedResponse;
import com.cjyljh.video2video.bean.PostVideoResponse;
import com.cjyljh.video2video.network.ResourceUtils;
import com.cjyljh.video2video.utils.DatabaseUtils;
import com.cjyljh.video2video.utils.NetworkUtils;
import com.cjyljh.video2video.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublishActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 101;

    private ImageView ivImage;
    private Button btnFromMobile;
    private Button btnFromCamera;
    private TextInputEditText inName;
    private TextInputEditText inDescription;
    private Button btnPublish;

    private Bitmap image;
    private String name;
    private String description;

    public Uri mSelectedImage;
    private Uri mSelectedVideo;

    private List<Call<PostVideoResponse>> mCallListPost = new ArrayList<>();
    private List<Call<FeedResponse>> mCallListFeed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        init();
    }

    private void init() {
        String strUri = getIntent().getStringExtra("strUri");
        mSelectedVideo = Uri.parse(strUri);

        ivImage = findViewById(R.id.iv_image);
        btnFromMobile = findViewById(R.id.btn_from_mobile);
        btnFromCamera = findViewById(R.id.btn_from_camera);
        inName = findViewById(R.id.in_name);
        inDescription = findViewById(R.id.in_description);
        btnPublish = findViewById(R.id.btn_publish);

        btnFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromCamera();
            }
        });

        btnFromMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromMobile();
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });
    }

    private File imgFile;

    private void takePicture() {
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        imgFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
        if (imgFile != null) {
            Uri uriFile = FileProvider.getUriForFile(getApplicationContext(), "com.cjyljh.video2video", imgFile);
            intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, uriFile);
            if (intentTakePicture.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intentTakePicture, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void getImageFromCamera() {
        if (ContextCompat.checkSelfPermission(PublishActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(PublishActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    PackageManager.PERMISSION_GRANTED);
        } else {
            takePicture();
        }
    }

    private void getImageFromMobile() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        intent.putExtra("crop", "true");
        //设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置裁剪图片宽高、
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 800);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);
            setPic();
        }
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK && data != null) {
            mSelectedImage = data.getData();
            Log.d("test_image", "uriImageSelect = [" + mSelectedImage.toString() + "]");
            Glide.with(getBaseContext()).load(mSelectedImage).into(ivImage);
//            try {
//                image = (Bitmap)data.getExtras().get("data");
//                ivImage.setImageBitmap(image);
//
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }

        }
    }

    private void setPic() {

        Log.d("TakePicture", "setPic: 裁切照片");

        int targetW = ivImage.getWidth();
        int targetH = ivImage.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        image = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
        ivImage.setImageBitmap(image);
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(PublishActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void publish() {

        btnPublish.setText("发布中...");
        btnPublish.setEnabled(false);

        name = String.valueOf(inName.getText());
        description = String.valueOf(inDescription.getText());
        MultipartBody.Part partVideo = getMultipartFromUri("video", mSelectedVideo);
        MultipartBody.Part partImage = getMultipartFromUri("cover_image", mSelectedImage);
        Call<PostVideoResponse> callPost = NetworkUtils.getResponseWithRetrofitAsyncPost(name, description, partImage, partVideo);
        mCallListPost.add(callPost);
        callPost.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                if (response.isSuccessful()) {
                    btnPublish.setText("发布成功");
                    Toast.makeText(PublishActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    Log.d("test", "onResponse: Successfully");

                    History historyT = new History();
                    Random randomT = new Random();
                    historyT.uriImage = mSelectedImage.toString();
                    historyT.uriVideo = mSelectedVideo.toString();
                    historyT.uploader = name;
                    historyT.description = description;
                    historyT.timePlay = randomT.nextInt(10);
                    historyT.timeUp = randomT.nextInt(10000);
                    DatabaseUtils.insertHistory(historyT);

                    MainActivity.main.mFragmentMe.refresh();

                } else {
                    btnPublish.setText("再次发布");
                    Toast.makeText(PublishActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                    Log.d("test", "onResponse: Failed");
                }

                btnPublish.setEnabled(true);

                mCallListPost.remove(call);
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {

                btnPublish.setText("再次发布");
                btnPublish.setEnabled(true);

                mCallListPost.remove(call);
            }
        });
    }
}
