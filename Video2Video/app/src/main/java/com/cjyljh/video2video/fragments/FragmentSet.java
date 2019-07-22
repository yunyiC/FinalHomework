package com.cjyljh.video2video.fragments;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cjyljh.video2video.MainActivity;
import com.cjyljh.video2video.PublishActivity;
import com.cjyljh.video2video.R;
import com.cjyljh.video2video.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @date 2019/7/20
 */
public class FragmentSet extends Fragment {
    private static final String TAG = FragmentSet.class.getSimpleName();

    private View mRootView = null;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    private ImageView mIvAdd;
    private ImageView mIvCameraRotate;

    private boolean isRecording = false;

    private int rotationDegree = 0;
    private int position = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int numCamera = 0;

    private MediaRecorder mMediaRecorder;

    private File fileVideo;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                Log.d("mPicture", "Error accessing file: " + e.getMessage());
            }

            mCamera.startPreview();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_set, container, false);

            mSurfaceView = mRootView.findViewById(R.id.sv_image);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (mCamera != null) {
                        try {
                            mCamera.setPreviewDisplay(holder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mCamera.startPreview();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (mCamera != null) {
                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                    }
                }
            });

            mIvAdd = mRootView.findViewById(R.id.iv_add);
            mIvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isRecording) {
                        //todo 停止录制
                        if (stopRecording() ) {
                            Toast.makeText(getContext(), "结束录制!", Toast.LENGTH_SHORT).show();
                            mIvAdd.setImageDrawable(getResources().getDrawable(R.drawable.button_play, null));
                            startPublishActivity();
                        }
                    } else {
                        //todo 录制
                        if (startRecording() ) {
                            Toast.makeText(getContext(), "开始录制!", Toast.LENGTH_SHORT).show();
                            mIvAdd.setImageDrawable(getResources().getDrawable(R.drawable.button_stop, null));

                        }
                    }
                }
            });

            mIvCameraRotate = mRootView.findViewById(R.id.iv_camera_rotate);
            mIvCameraRotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "切换摄像头!", Toast.LENGTH_SHORT).show();
                    switch(position) {
                        case Camera.CameraInfo.CAMERA_FACING_BACK :
                            position = Camera.CameraInfo.CAMERA_FACING_FRONT;
                            break;
                        case Camera.CameraInfo.CAMERA_FACING_FRONT:
                            position = Camera.CameraInfo.CAMERA_FACING_BACK;
                            break;
                        default:
                            position = Camera.CameraInfo.CAMERA_FACING_BACK;
                            break;
                    }
                    mCamera = getCamera(position);
                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();
                }
            });
        }

        return mRootView;
    }

    private void startPublishActivity() {

        //closeCamera();

        Uri uri = Uri.fromFile(fileVideo);
        String strUri = uri.toString();
        Log.d("test", "startPublishActivity: uri = " + strUri);
        Intent intent = new Intent(getContext(), PublishActivity.class);
        intent.putExtra("strUri", strUri);
        startActivity(intent);
    }

    public void openCamera() {
        Log.d(TAG, "openCamera: 相机打开!!!");
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        mCamera = getCamera(position);
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        }
    }

    public void closeCamera() {
        Log.d(TAG, "openCamera: 相机关闭!!!");
        releaseCameraAndPreview();
    }

    private boolean startRecording() {
        if (mCamera == null) {
            mCamera = getCamera(position);
            if (mCamera == null) {
                return false;
            }
        }

        isRecording = true;
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        fileVideo = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile(fileVideo.toString());
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean stopRecording() {
        if (mCamera == null) {
            return false;
        }

        isRecording = false;
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
        return true;
    }

    public Camera getCamera(int position) {
        numCamera = Camera.getNumberOfCameras();
        if (numCamera <= position) {
            return null;
        }
        //处理mCamera,使其为null
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        mCamera = Camera.open(position);
        rotationDegree = getCameraDisplayOrientation(position);
        mCamera.setDisplayOrientation(rotationDegree);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode("macro");
        mCamera.setParameters(parameters);

        return mCamera;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = MainActivity.main.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            // compensate the mirror
            result = (DEGREE_360 - result) % DEGREE_360;
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        ///
    }


}
