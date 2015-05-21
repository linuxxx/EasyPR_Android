# EasyPR_Android
EasyPR_Android本是EasyPR的android移植版本，参考了taotao1233的那个android移植，在此一并感谢。

#注意MainActivity下
		String svmpath = path+"/svm.xml";
		String annpath = path+"/ann.xml";
		String imgpath = path+"/plate_locate.jpg";
将项目jni/model 下的svm.xml和ann.xml和测试图片plate_locate.jpg导入你手机的SD卡根目录。我这里用DDMS导入文件。

#使用步骤
* 下载并导入OpenCV-2.4.10-android-sdk项目到eclipse，设置为is Library
* 导入EasyPR_Android该项目，添加引用
右键properties-> Android-> library-> add OpenCV-2.4.10-android-sdk
* 修改jni/android.mk
把里面的路径替换成你本机的路径：include D:/OpenCV-2.4.10-android-sdk/OpenCV-2.4.10-android-sdk/sdk/native/jni/OpenCV.mk

* 重新编译
cmd 到jni目录，执行ndk-build

* 导入libs
把OpenCV-2.4.2-android-sdk/sdk/native/libs/下的文件复制到libs/

#其他
有任何问题请发issues，或者联系我unix_x@qq.com | QQ:415435551  :)



