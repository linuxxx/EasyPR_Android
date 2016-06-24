package com.aiseminar.EasyPR;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import com.aiseminar.platerecognizer.R;
import com.aiseminar.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by ares on 6/19/16.
 */
public class PlateRecognizer {
    private Context mContext;
    private String mSvmpath = null;
    private String mAnnpath = null;
    private boolean mRecognizerInited = false;
    private long mRecognizerPtr = 0;

    public PlateRecognizer(Context context) {
        mContext = context;
        if (checkAndUpdateModelFile()) {
            mRecognizerPtr = initPR(mSvmpath, mAnnpath);
            if (0 != mRecognizerPtr) {
                mRecognizerInited = true;
            }
        }
    }

    protected void finalize() {
        uninitPR(mRecognizerPtr);
        mRecognizerPtr = 0;
        mRecognizerInited = false;
    }

    public boolean checkAndUpdateModelFile() {
        if (null == mContext) {
            return false;
        }

        mSvmpath = FileUtil.getMediaFilePath(FileUtil.FILE_TYPE_SVM_MODEL);
        mAnnpath = FileUtil.getMediaFilePath(FileUtil.FILE_TYPE_ANN_MODEL);

        //如果模型文件不存在从APP的资源中拷贝
        File svmFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_SVM_MODEL);
        File annFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_ANN_MODEL);
        if (/*! svmFile.exists()*/true) {
            try {
                InputStream fis = mContext.getResources().openRawResource(R.raw.svm);
                FileOutputStream fos = new FileOutputStream(svmFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (FileNotFoundException e) {
                Log.d("PlateRecognizer", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PlateRecognizer", "Error accessing file: " + e.getMessage());
            }
        }
        if (/*! annFile.exists()*/true) {
            try {
                InputStream fis = mContext.getResources().openRawResource(R.raw.ann);
                FileOutputStream fos = new FileOutputStream(annFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (FileNotFoundException e) {
                Log.d("PlateRecognizer", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PlateRecognizer", "Error accessing file: " + e.getMessage());
            }
        }

        if (svmFile.exists() && annFile.exists()) {
            return true;
        }
        return false;
    }

    public String recognize(String imagePath) {
        //判断文件夹是否存在
        File imageFile = new File(imagePath);
        if (! mRecognizerInited || ! imageFile.exists()) {
            return null;
        }

        if (0 == mRecognizerPtr) {
            return null;
        }

        byte[] retBytes = plateRecognize(mRecognizerPtr, imagePath);
        String result = null;
        try {
            result = new String(retBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * JNI Functions
     */
    // 加载车牌识别库
    static {
        try {
            System.loadLibrary("EasyPR");
        } catch (UnsatisfiedLinkError ule) {
            System.err.println("WARNING: Could not load EasyPR library!");
        }
    }

    public static native String stringFromJNI();
    public static native long initPR(String svmpath, String annpath);
    public static native long uninitPR(long recognizerPtr);
    public static native byte[] plateRecognize(long recognizerPtr, String imgpath);
}
