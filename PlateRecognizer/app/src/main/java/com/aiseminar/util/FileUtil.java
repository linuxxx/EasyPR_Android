package com.aiseminar.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Created by ares on 6/20/16.
 */
public class FileUtil {
    public static final int FILE_TYPE_IMAGE = 1;
    public static final int FILE_TYPE_PLATE = 2;
    public static final int FILE_TYPE_SVM_MODEL = 3;
    public static final int FILE_TYPE_ANN_MODEL = 4;

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = null;
        switch (type) {
            case FILE_TYPE_IMAGE: {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "PlateRcognizer");
                break;
            }
            case FILE_TYPE_PLATE: {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "PlateRcognizer/PlateRect");
                break;
            }
            case FILE_TYPE_ANN_MODEL:
            case FILE_TYPE_SVM_MODEL: {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "PlateRcognizer");
                break;
            }
            default:
                return null;
        }
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                Log.d("PlateRcognizer", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = DateUtil.getDateFormatString(new Date());
        File mediaFile;
        switch (type) {
            case FILE_TYPE_IMAGE: {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "RPK_" + timeStamp + ".jpg");
                break;
            }
            case FILE_TYPE_PLATE: {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "RP_" + timeStamp + ".jpg");
                break;
            }
            case FILE_TYPE_ANN_MODEL: {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "ann.xml");
                break;
            }
            case FILE_TYPE_SVM_MODEL: {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "svm.xml");
                break;
            }
            default:
                return null;
        }

        return mediaFile;
    }

    public static String getMediaFilePath(int type) {
        File mediaStorageDir = null;
        File mediaFile;
        switch (type) {
            case FILE_TYPE_ANN_MODEL: {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "PlateRcognizer");
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "ann.xml");
                break;
            }
            case FILE_TYPE_SVM_MODEL: {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "PlateRcognizer");
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "svm.xml");
                break;
            }
            default:
                return null;
        }
        return mediaFile.getAbsolutePath();
    }
}
