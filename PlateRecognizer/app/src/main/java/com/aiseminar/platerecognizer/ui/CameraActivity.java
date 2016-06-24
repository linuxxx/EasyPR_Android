package com.aiseminar.platerecognizer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiseminar.EasyPR.PlateRecognizer;
import com.aiseminar.platerecognizer.R;
import com.aiseminar.platerecognizer.base.BaseActivity;
import com.aiseminar.util.BitmapUtil;
import com.aiseminar.util.DateUtil;
import com.aiseminar.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback {

    @Bind(R.id.svCamera)
    SurfaceView mSvCamera;

    @Bind(R.id.ivPlateRect)
    ImageView mIvPlateRect;

    @Bind(R.id.ivCapturePhoto)
    ImageView mIvCapturePhoto;

    @Bind(R.id.tvPlateResult)
    TextView mTvPlateResult;

    private static final String TAG = CameraActivity.class.getSimpleName();

    private int cameraPosition = 0; // 0表示后置，1表示前置

    private SurfaceHolder mSvHolder;
    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private MediaPlayer mShootMP;
    private PlateRecognizer mPlateRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ButterKnife.bind(this);

        mPlateRecognizer = new PlateRecognizer(this);

        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.checkCameraHardware(this) && (mCamera == null)) {
            // 打开camera
            mCamera = getCamera();
            // 设置camera方向
            mCameraInfo = getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK);
            if (null != mCameraInfo) {
                adjustCameraOrientation();
            }

            if (mSvHolder != null) {
                setStartPreview(mCamera, mSvHolder);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         * 记得释放camera，方便其他应用调用
         */
        releaseCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化相关data
     */
    private void initData() {
        // 获得句柄
        mSvHolder = mSvCamera.getHolder(); // 获得句柄
        // 添加回调
        mSvHolder.addCallback(this);
    }

    private Camera getCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            camera = null;
            Log.e(TAG, "Camera is not available (in use or does not exist)");
        }
        return camera;
    }

    private Camera.CameraInfo getCameraInfo(int facing) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return cameraInfo;
            }
        }
        return null;
    }

    private void adjustCameraOrientation() { // 调整摄像头方向
        if (null == mCameraInfo || null == mCamera) {
            return;
        }

        int orientation = this.getWindowManager().getDefaultDisplay().getOrientation();
        int degrees = 0;

        switch (orientation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            // back-facing
            result = (mCameraInfo.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    /**
     * 释放mCamera
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();// 停掉原来摄像头的预览
            mCamera.release();
            mCamera = null;
        }
    }

    @OnClick({R.id.ivCapturePhoto})
    public void onClick(View view) {
        switch (view.getId()) {
            case 999: // R.id.id_switch_camera_btn:
                // 切换前后摄像头
                int cameraCount = 0;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数

                for (int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
                    if (cameraPosition == 1) {
                        // 现在是后置，变更为前置
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            /**
                             * 记得释放camera，方便其他应用调用
                             */
                            releaseCamera();
                            // 打开当前选中的摄像头
                            mCamera = Camera.open(i);
                            // 通过surfaceview显示取景画面
                            setStartPreview(mCamera,mSvHolder);
                            cameraPosition = 0;
                            break;
                        }
                    } else {
                        // 现在是前置， 变更为后置
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            /**
                             * 记得释放camera，方便其他应用调用
                             */
                            releaseCamera();
                            mCamera = Camera.open(i);
                            setStartPreview(mCamera,mSvHolder);
                            cameraPosition = 1;
                            break;
                        }
                    }

                }
                break;
            case R.id.ivCapturePhoto:
                // 拍照,设置相关参数
//                Camera.Parameters params = mCamera.getParameters();
//                params.setPictureFormat(ImageFormat.JPEG);
//                DisplayMetrics metric = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(metric);
//                int width = metric.widthPixels;  // 屏幕宽度（像素）
//                int height = metric.heightPixels;  // 屏幕高度（像素）
//                params.setPreviewSize(width, height);
//                // 自动对焦
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                mCamera.setParameters(params);
                try {
                    mCamera.takePicture(shutterCallback, null, jpgPictureCallback);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }

                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera, mSvHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mSvHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        setStartPreview(mCamera, mSvHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 当surfaceview关闭时，关闭预览并释放资源
        /**
         * 记得释放camera，方便其他应用调用
         */
        releaseCamera();
        holder = null;
        mSvCamera = null;
    }

    /**
     * TakePicture回调
     */
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            shootSound();
            mCamera.setOneShotPreviewCallback(previewCallback);
        }
    };

    Camera.PictureCallback rawPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
        }
    };

    Camera.PictureCallback jpgPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();

            File pictureFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                // 照片转方向
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap normalBitmap = BitmapUtil.createRotateBitmap(bitmap);
//                fos.write(data);
                normalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                // 更新图库
                // 把文件插入到系统图库
//                try {
//                    MediaStore.Images.Media.insertImage(CameraActivity.this.getContentResolver(),
//                            pictureFile.getAbsolutePath(), pictureFile.getName(), "Photo taked by RoadParking.");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                // 最后通知图库更新
                CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + pictureFile.getAbsolutePath())));
                Toast.makeText(CameraActivity.this, "图像已保存。", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * activity返回式返回拍照图片路径
     * @param mediaFile
     */
    private void returnResult(File mediaFile) {
//        Intent intent = new Intent();
//        intent.setData(Uri.fromFile(mediaFile));
//        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 设置camera显示取景画面,并预览
     * @param camera
     */
    private void setStartPreview(Camera camera,SurfaceHolder holder){
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     *   播放系统拍照声音
     */
    private void shootSound() {
        AudioManager meng = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (mShootMP == null)
                mShootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mShootMP != null)
                mShootMP.start();
        }
    }

    /**
     * 获取Preview界面的截图，并存储
     */
    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // 获取Preview图片转为bitmap并旋转
            Camera.Size size = mCamera.getParameters().getPreviewSize(); //获取预览大小
            final int w = size.width;  //宽度
            final int h = size.height;
            final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
            // 转Bitmap
            ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
            if(! image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                return;
            }
            byte[] tmp = os.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
            Bitmap rotatedBitmap = BitmapUtil.createRotateBitmap(bitmap);

            cropBitmapAndRecognize(rotatedBitmap);
        }
    };

    public void cropBitmapAndRecognize(Bitmap originalBitmap) {
        // 裁剪出关注区域
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        Bitmap sizeBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);

        int rectWidth = (int)(mIvPlateRect.getWidth() * 1.5);
        int rectHight = (int)(mIvPlateRect.getHeight() * 1.5);
        int[] location = new int[2];
        mIvPlateRect.getLocationOnScreen(location);
        location[0] -= mIvPlateRect.getWidth() * 0.5 / 2;
        location[1] -= mIvPlateRect.getHeight() * 0.5 / 2;
        Bitmap normalBitmap = Bitmap.createBitmap(sizeBitmap, location[0], location[1], rectWidth, rectHight);

        // 保存图片并进行车牌识别
        File pictureFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_PLATE);
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }

        try {
            mTvPlateResult.setText("正在识别...");
            FileOutputStream fos = new FileOutputStream(pictureFile);
            normalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            // 最后通知图库更新
            CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + pictureFile.getAbsolutePath())));

            // 进行车牌识别
            String plate = mPlateRecognizer.recognize(pictureFile.getAbsolutePath());
            if (null != plate && ! plate.equalsIgnoreCase("0")) {
                mTvPlateResult.setText(plate);
            } else {
                mTvPlateResult.setText("请调整角度");
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

}
