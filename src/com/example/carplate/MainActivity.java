package com.example.carplate;

import java.io.UnsupportedEncodingException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private ImageView imageView = null;
	private Bitmap bmp = null;
	private TextView m_text = null;
	private String path = null;
	
	static {
	    if (!OpenCVLoader.initDebug()) {
	    } else {
	        System.loadLibrary("imageproc");
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.image_view);
		m_text = (TextView) findViewById(R.id.myshow);
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.plate_locate);
		imageView.setImageBitmap(bmp);
		path = Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public void click(View view) throws UnsupportedEncodingException {
		System.out.println("entering the jni");
		String svmpath = path+"/svm.xml";
		String annpath = path+"/ann.xml";
		String imgpath = path+"/plate_locate.jpg";
		byte[] resultByte = CarPlateDetection.ImageProc(imgpath,svmpath,annpath);
		String result = new String(resultByte,"GBK");
		System.out.println(result);
		m_text.setText(result);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
	}
}
