# FFmpegStudy
### 下载FFmpeg源码和ndk
### 环境配置
### 编译FFmpeg源码 生成Android需要的动态库或静态库
#### FFmpeg编译选项说明
 1. --prefix 
  &emsp;&emsp; 指定输出目录
 2. --enable-small
 &emsp;&emsp;优化大小
 3.  --target-os=android \
  &emsp;&emsp;指定编译的目标平台
 4.  --disable-static \
 &emsp;&emsp;//关闭静态库 
 5.  --enable-shared \
 &emsp;&emsp;指定编译成动态库
 6.  --enable-small \
 7.  --disable-programs \
  &emsp;&emsp;不编译ffmpeg程序（命令行工具），我们是需要获得静态（动态）库
 8.  --disable-ffmpeg \
 9.  --disable-ffplay \
 10. --disable-ffprobe \
 11. --disable-doc \
 12. --disable-symver \
 13. --disable-asm \
 14. --disable-avdevice
 &emsp;&emsp;关闭avdevice模块，次模块在Android中无用
 15. --disable-encoders
 &emsp;&emsp;关闭所有编译器（播放时不需要编码）
 16. --disable-muxers
 &emsp;&emsp;关闭所有复用器（封装器），不需要生成mp4这样的文件，可以关闭
 17. --disable-filter
 &emsp;&emsp;关闭视频滤镜
### 创建Android studio ndk项目引入ffmpeg库 配置ffmpeg开发环境 
