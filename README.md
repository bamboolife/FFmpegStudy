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
## FFmpeg常用命令
### 基本信息查询命令
### 录制命令
> ffmpeg -f gdigrab -framerate 30 -offset_y 0 -video_size 1920x1080 -i desktop out.mpg
### 处理原始数据命令
### 裁剪与合并命令
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
