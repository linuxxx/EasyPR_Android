# EasyPR_Android
本程序基于对Fork代码的学习，结合新版的EasyPR1.4和OpenCV3.1.0进行移植。
开发环境为Android Studio.

#说明
1. 程序直接集成了OpencvNative 3.1.0库，无需下载OpenCV。
2. 本程序直接将模型文件放置在res/raw目录下，在运行时自行拷贝到手机文件中，无需模型文件的手动放置。
3. 本程序运行时，有车牌限定框，APP会在框的范围内进行图像裁剪，人为缩小了识别范围，提高识别度。但是，这与算法的优劣无关。研究人员，还是用全图进行识别才能体验算法优劣。

#鸣谢
感谢贡献EasyPR和EasyPR_Android的开发者，我也是站着他们的肩膀上继续前行，谢谢。
