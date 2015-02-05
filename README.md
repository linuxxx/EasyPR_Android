# EasyPR_Android
EasyPR_Android本是EasyPR的android移植版本，参考了taotao1233的那个android移植，在此一并感谢。

#注意
		String svmpath = path+"/svm.xml";
		String annpath = path+"/ann.xml";
		String imgpath = path+"/plate_locate.jpg";
将项目jni/model 下的svm.xml和ann.xml和测试图片 导入SD卡。

#使用步骤
* 导入OpenCV-2.4.10-android-sdk项目到eclipse，设置为is Library
* 导入该项目，右键properties-> Android-> library-> add OpenCV-2.4.10-android-sdk
* 修改android.mk：include D:/OpenCV-2.4.10-android-sdk/OpenCV-2.4.10-android-sdk/sdk/native/jni/OpenCV.mk
* 重新编译（我这里是使用ndk-build）
* 运行APP
* 手机上需要安装OpenCV Manager，运行项目会提示下载，点击下载即可

#其他
有任何问题请发issues，或者联系我o.ounix@gmail.com :)



