# FFmpegStudy
## 什么是FFmpeg
- ffmpeg是一套可以用来记录、转换数字音频、视频，并能将其转化为流的开源计算机程序
- ffmpeg是一个多媒体视频处理工具，有非常强大的功能包括视频的采集功能、视频格式转化、视频抓图、给视频添加水印等
## FFmpeg组成
### FFmpeg工具
- ffmpeg
- ffplay
- ffprobe
### FFmpeg开发库
- Libavcodec
- Libavutil
- Libavformat
- Libavdevice
- Libavfilter
- Libswscale
- Libswresample
## 如何使用FFmpeg
>ffmpeg是由c代码编写而成，功能多，代码量大。代码开源，需要先编译，后使用，编译可以通过makefile语法来进行编译
## FFmpeg的应用
- 直播
音视频会议教育直播，娱乐、游戏类直播
- 短视频
抖音、微视、火山小视频
- 网络视频
爱奇艺、优酷、腾讯视频
- 音视频通话
微信、qq、facedbook
- 视频监控
幼儿园、保安
- 人工智能
人脸识别，智能印象
## FFmpeg常用命令
### 基本信息查询命令
| 参数       |  说明  |
|:------    |:------|
|-version | 显示版本 |
|-demuxers  | 显示可用的demuxers |
|-muxers | 显示可用的muxers |
|-devices | 显示可用的设备 |
|-codecs | 显示可用的编码器 |
|-decoders | 显示所有的编码器 |
|-bsfs | 显示比特流filter |
|-formats | 显示可用的格式 |
|-protocos | 显示可用的过滤器 |
|-pix_fmts | 显示可用的像素格式 |
|-sample_fmts | 显示可用的采样格式 |
|-layouts | 显示channel名称 |
|-colors | 显示识别的颜色名称 |
|-filters | 显示可用的过滤器 |

### 录制命令
```
 ffmpeg -f gdigrab -framerate 30 -offset_y 0 -video_size 1920x1080 -i desktop out.mpg
```
| 参数       |  说明  |
|:------    |:------|
| -gdigrad   | 表明我们是通过gdi抓屏的方式；（mac下 avfoundation） |
| -framerate 30 | 表示我录制的帧率为30 |
| -offset_x | 左上偏移量x |
| -offset_y | 右上偏移量Y |
| -vide_size | 需要录制的宽度和高度，这是整个屏幕 |
| -i | 输入路径和名称以及各式mpg |
| -desktop | 告诉ffmpeg我们录的是屏幕，而不是一个窗口（可以录制一个窗口，不过得用窗口的ID）|
### 处理原始数据命令
>定义：获取未经过编码的画像和音频。画面信息（一般是yuv）音频信息是（pcm）

提取YUV数据
```
ffmpeg -i input.mp4 -an -c:v rawvideo -pix_fmt yuv420p out.yuv
```
| 参数       |  说明  |
|:------    |:------|
| -c:v rawvideo   | 指定将视频转成原始数据 |
| -pixel_format yuv420p | 指定转换各式为yuv420p |
> 说明：未经过编码的数据的需要用到ffplay播放（ffpeg -s 608*368 out.yuv）

提取PCM数据
```
ffmpeg -i input.mp4 -vn -ar 44100 -ac 2 -f s16le out.pcm
```
| 参数       |  说明  |
| ------    | ------:|
| -ar   | 指定音频采样率44100即为44.1HZ |
| -ac   | 指定音频声道channel 2为双声道 |
| -f    | 数据存储各式 s:Sigend有符号的，16：每一个数值用16位表示，l ：little，e：end |
> 说明：未经过编码的数据需要用到ffplay播放（ffplay -ar 44100 -ac 2 -f s16le out.pcm）

### 裁剪与合并命令
分解复用命令
1. 抽取音频流
```
 ffmpeg -i input.mp4 -acodec copy -vn out.aac
```
| 参数       |  说明  |
|:------    |:------|
| -acodec   | 指定音频编码器 |
| copy      | 指明只拷贝，不做编解码 |
| -vn       | v代表视频，n代表no也就是无视频的意思 |

2. 抽取视频流
```
 ffmpeg -i input.mp4 -vcodec copy -an out.h264
```
| 参数       |  说明  |
|:------    |:------|
| -vcodec   | 指定视频编码器 |
| copy      | 指明只拷贝，不做编解码 |
| -an       | a代表音频，n代表no也就是无音频的意思 |
3. 合成视频
```
 ffmpeg -i out.h264 -i out.aac -vcodec copy -acodec copy out.mp4
```
### 直播命令
### 各种滤镜命令
### 视频转码
```
ffmpeg -i out.mp4 -vcodec copy -acodec copy intput.flv
```

### 下载FFmpeg源码和ndk
### 环境配置
### 编译FFmpeg源码 生成Android需要的动态库或静态库
### FFmpeg编译选项说明
 1. --prefix 
  &emsp;&emsp; 指定输出目录
 2. --enable-small
 &emsp;&emsp;优化大小
 3.  --disable-static \
 &emsp;&emsp;//关闭静态库 
 4.  --enable-shared \
 &emsp;&emsp;指定编译成动态库
 5.  --enable-small \
 6.  --disable-programs \
  &emsp;&emsp;不编译ffmpeg程序（命令行工具），我们是需要获得静态（动态）库
 7.  --disable-ffmpeg \
 8.  --disable-ffplay \
 9. --disable-ffprobe \
 10. --disable-doc \
 11. --disable-symver \
 12. --disable-asm \
 13. --disable-avdevice
 &emsp;&emsp;关闭avdevice模块，次模块在Android中无用
 14. --disable-encoders
 &emsp;&emsp;关闭所有编译器（播放时不需要编码）
 15. --disable-muxers
 &emsp;&emsp;关闭所有复用器（封装器），不需要生成mp4这样的文件，可以关闭
 16. --disable-filter
 &emsp;&emsp;关闭视频滤镜
 17. --cross-prefix=$CROSS_PREFIX \
   关键点1.指定交叉编译工具目录
 18. --target-os=android \
   关键点2.指定目标平台为android
 19. --arch=$ARCH \
   关键点3.指定cpu类型
 20. --cpu=$CPU \
   #关键点4.指定cpu架构
 21. --cc=$CC
      --cxx=$CXX
    超级关键点5.指定c语言编译器
 22. --enable-cross-compile \
   关键点6.开启交叉编译
  23. 超级关键7.配置编译环境c语言的头文件环境
    --sysroot=$SYSROOT \
    --extra-cflags="-Os -fpic $OPTIMIZE_CFLAGS" \
    --extra-ldflags="$ADDI_LDFLAGS" \
    $ADDITIONAL_CONFIGURE_FLAG
### 创建Android studio ndk项目引入ffmpeg库 配置ffmpeg开发环境 
