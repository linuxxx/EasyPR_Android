package com.example.carplate;


public class CarPlateDetection {
	public static native byte[] ImageProc(String imgpath, String svmpath, String annpath);
}
