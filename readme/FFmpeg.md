# FFmpeg文件说明

## FFmpeg 文件结构
### libavcodec
<br>主要存放 ffmpeg 支持的 各种编解码器 的实现及 ffmpeg 编解码 功能相关的数
<br>文件	简要说明
<br>allcodecs.c	简单的注册类函数
<br>avcodec.h	编解码相关结构体定义和函数原型声明
<br>dsputil.c	限幅数组初始化
<br>dsputil.h	限幅数组声明
<br>imgconvert.c	颜色空间转换相关函数实现
<br>imgconvert_template.h	颜色空间转换相关结构体定义和函数声明
<br>utils_codec.c	一些解码相关的工具类函数的实现
<br>mpeg4audio.c	mpeg4 音频编解码器的函数实现
<br>mpeg4audio.h	mpeg4 音频编解码器的函数声明
<br>mpeg4data.h	mpeg4 音视频编解码器的公用的函数声明及数据结构定义
<br>mpeg4video.c	mpeg4 视频编解码器的函数实现
<br>mpeg4video.h	mpeg4 视频编解码器的函数的声明及先关数据结构的定义
<br>mpeg4videodec.c	mpeg4 视频解码器的函数实现
<br>mpeg4videoenc.c	mpeg4 视频编码器的函数实现
### libavformat
<br>本目录主要存 放 FFMPEG 支持 的各种媒体格 式 MUXER/DEMUXER 和数据流协议 的定义和实现 文件以及 ffmpeg 解复用 相关的数据结 构及函数定义

<br>文件	简要说明
<br>allformats.c	简单注册类函数
<br>avformat.h	文件和媒体格式相关函数声明和数据结 构定义
<br>avio.c	无缓冲 IO 相关函数实现
<br>avio.h	无缓冲 IO 相关结构定义和函数声明
<br>aviobuf.c	有缓冲数据 IO 相关函数实现
<br>cutils.c	简单的字符串操作函数
<br>utils_format.c	文件和媒体格式相关的工具函数的实现
<br>file.c	文件 io 相关函数
<br>……	其他相关媒体流 IO 的函数和数据结构实 现文件。 如：rtsp、http 等。
<br>avi.c	AVI 格式的相关函数定西
<br>avi.h	AVI 格式的相关函数声明及数据结构定义
<br>avidec.c	AVI 格式 DEMUXER 相关函数定义
<br>avienc.c	AVI 格式 MUXER 相关函数定义
<br>……	其他媒体格式的 muxer/demuxer 相关函 数及数据结构定义和声明文件
### libavutil
<br>主要存放 ffmpeg 工具类 函数的定义

<br>avutil.h	简单的像素格式宏定义
<br>bswap.h	简单的大小端转换函数的实现
<br>commom.h	公共的宏定义和简单函数的实现
<br>mathematics.c	数学运算函数实现
<br>rational.h	分数相关表示的函数实现

##  I\O 模块分析
### 一概述
ffmpeg 项目的数据 IO 部分主要是在 libavformat 库中实现， 某些对于内存的操作部分在 libavutil 库中。数据 IO 是基于文件格式
（Format）以及文件传输协议(Protocol) 的， 与具体的编解码标准无关。 ffmpeg 工程转码时数据 IO 层次关系如图所示：

![image]()

对于上面的数据 IO 流程， 具体可以用下面的例子来说明， 我们从一个 http 服务器 获取音视频数据， 格式是 flv 的， 需要通过转码后变成 avi 格式， 然后通过 udp 协议进 行发布。 其过程就如下所示：
 
 1. 读入 http 协议数据流， 根据 http 协议获取真正的文件数据（去除无关报文信 息）；
 2. 根据 flv 格式对数据进行解封装；
 3. 读取帧进行转码操作；
 4. 按照目标格式 avi 进行封装；
 5. 通过 udp 协议发送出去。
 
 ### 二相关数据结构介绍
 
 在 libavformat 库中与数据 IO 相关的数据结构主要有 URLProtocol、URLContext、ByteIOContext、AVFormatContext 等， 各结构之间的关系如图所示。
 
 ![image]()
 
 1. URLProtocol 结构
 
 表示广义的输入文件， 该结构体提供了很多的功能函数， 每一种广义的输入文件 （如：file、pipe、tcp、rtp 等等）对应着一个 URLProtocol 结构，
 在 av_register_all() 中将该结构体初始化为一个链表， 表头为 avio.c 里的 URLProtocol *first_protocol = NULL;保存所有支持的输入文件协议，该结构体的定义如下：
```
typedef struct URLProtocol 
{ 
const char *name; 
int (*url_open)(URLContext *h， const char *url， int flags); 
int (*url_read)(URLContext *h， unsigned char *buf， int size);
int (*url_write)(URLContext *h， const unsigned char *buf， int size); 
int64_t (*url_seek)(URLContext *h， int64_t pos， int whence); 
int (*url_close)(URLContext *h); 
struct URLProtocol *next; 
int (*url_read_pause)(URLContext *h， int pause);
int64_t (*url_read_seek)(URLContext *h， int stream_index，int64_t timestamp， int flags);
int (*url_get_file_handle)(URLContext *h);
int priv_data_size;
const AVClass *priv_data_class; 
int flags;
int (*url_check)(URLContext *h， int mask);
} URLProtocol;
```

注意到， URLProtocol 是一个链表结构， 这是为了协议的统一管理， ffmpeg 项目中 将所有的用到的协议都存放在一个全局变量 first_protocol 中， 协议的注册是在 av_register_all 中完成的， 新添加单个协议可以调用 av_register_protocol2 函数实 现。 而协议的注册就是将具体的协议对象添加至 first_protocol 链表的末尾。

URLProtocol 在各个具体的文件协议中有一个具体的实例，如在 file 协议中定义为：

```
URLProtocol ff_file_protocol = {
 .name = " file" ，
 .url_open = file_open，
 .url_read = file_read，
 .url_write = file_write，
 .url_seek = file_seek，
 .url_close = file_close，
 .url_get_file_handle = file_get_handle， .
 .url_check = file_check,
};
 
``` 
2. URLContext 结构

URLContext 提供了与当前打开的具体的文件协议（URL）相关数据的描述， 在该结 构中定义了指定当前 URL（即 filename 项）所要用到的具体的 URLProtocol， 即：提供 了一个在 URLprotocol 链表中找到具体项的依据， 此外还有一些其它的标志性的信息， 如 flags， is_streamed 等。 它可以看成某一种协议的载体。 其结构定义如下：

```
typedef struct URLContext
 {
const AVClass *av_class; ///< information for av_log(). Set by url_open(). 
struct URLProtocol *prot; 
int flags; 
int is_streamed; /**< true if streamed (no seek possible)， default = false *
int max_packet_size; void *priv_data; 
char *filename; /**< specified URL */ 
int is_connected;
 } URLContext;
 
 ```
 
 那么 ffmpeg 依据什么信息初始化 URLContext？然后又是如何初始化 URLContext 的呢？

在打开一个 URL 时， 全局函数 ffurl_open 会根据 filename 的前缀信息来确定 URL 所使用的具体协议， 并为该协议分配好资源， 再调用 ffurl_connect 函数打开具体协议， 即调用协议的 url_open， 调用关系如下：

```
int av_open_input_file(AVFormatContext **ic_ptr，
const char *filename，
AVInputFormat *fmt，
int buf_size，
AVFormatParameters *ap)

int avformat_open_input(
AVFormatContext **ps ，
const char *filename ，
AVInputFormat *fmt，
AVDictionary **options)
```

3. AVIOContext 结构
AVIOContext（即：ByteIOContext）是由 URLProtocol 和 URLContext 结构扩展而 来，也是 ffmpeg 提供给用户的接口，它将以上两种不带缓冲的读取文件抽象为带缓冲的 读取和写入， 为用户提供带缓冲的读取和写入操作。 数据结构定义如下：
```
typedef struct 
{
  unsigned char *buffer; /**< Start of the buffer. */
  int buffer_size; /**< Maximum buffer size */ 
  unsigned char *buf_ptr; /**< Current position in the buffer */
  unsigned char *buf_end; 
  void *opaque; /关联 URLContext int (*read_packet)(void *opaque， uint8_t *buf， int buf_size); 
  int (*write_packet)(void *opaque， uint8_t *buf， int buf_size);
  int64_t (*seek)(void *opaque， int64_t offset， int whence); 
  int64_t pos; int must_flush; int eof_reached; /**< true if eof reached */
  int write_flag; /**< true if open for writing */
  int max_packet_size; 
  unsigned long checksum; 
  unsigned char *checksum_ptr; 
  unsigned long (*update_checksum)(unsigned long checksum， const uint8_t *buf， unsigned int size);
  int error; 
  int (*read_pause)(void *opaque， int pause) int64_t (*read_seek)(void *opaque， int stream_index，int64_t timestamp， int flags); 
  int seekable;
 } AVIOContext;

static int init_input(AVFormatContext *s， const char *filename)
```

结 构 简 单 的 为 用 户 提 供 读 写 容 易 实 现 的 四 个 操 作 ， read_packet write_packet read_pause read_seek， 极大的方便了文件的读取， 四
个函数在加了缓冲机制后被中转 到， URLContext 指向的实际的文件协议读写函数中。

所以， 可以简要的描述为：AVIOContext 的接口口是加了缓冲后的 URLProtocol 的 函数接口。

##  Demuxer 和 muxer 模块分析

### 概述
ffmpeg 的 demuxer 和 muxer 接口分别在 AVInputFormat 和 AVOutputFormat 两个结

构体中实现， 在 av_register_all()函数中将两个结构分别静态初始化为两个链表， 保 存在全局变量：
first_iformat 和 first_oformat 两个变量中。在 FFmpeg 的文件转换或 者打开过程中， 首先要做的就是根据
传入文件和传出文件的后缀名匹配合适的 demuxer 和 muxer， 得到合适的信息后保存在 AVFormatContext 中。

### 相关数据结构介绍
1. AVInputFormat
该结构被称为 demuxer， 是音视频文件的一个解封装器

对于不同的文件格式要实现相应的函数接口， 这样每一种格式都有一个对应的 demuxer， 所有的 demuxer 都保存在全局变量 first_iformat 中。 
红色表示提供的接口。

2. AVOutputFormat
该结构与 AVInputFormat 类似也是在编译时静态初始化， 组织为一个链表结构， 提 供了多个 muxer 的函数接口。

对于不同的文件格式要实现相应的函数接口， 这样每一种格式都有一个对应的 muxer， 所有的 muxer 都保存在全局变量 first_oformat 中。

3. AVFormatContext
该结构表示与程序当前运行的文件容器格式使用的上下文， 着重于所有文件容器共 有的属性，在运行时动态的确定其值，是 AVInputFormat 和 AVOutputFormat 的载体，但 同一个结构对象只能使 AVInputFormat 和 AVOutputFormat 中的某一个有效。每一个输入 和输出文件， 都在
`static AVFormatContext *output_files[MAX_FILES] `
和
`static AVFormatContext *input_files[MAX_FILES];`
定义的指针数组全局变量中有对应的实体。 对于输入和输出， 因为共用的是同一个结构 体， 所以需要分别对该结构中如下定义的 iformat 或 oformat 成员赋值。 
在转码时读写 数据是通过 AVFormatContext 结构进行的。

由上可见， 对 AVFormatContext 的读写操作最终是通过 ByteIOContext 来实现的， 这样， AVFormatContext 与 URLContext 就由 ByteIOContext 结构联系到一起了。 在 AVFormat 结构体中有一个 packet 的缓冲区 raw_packet_buffer， 是 AVPackList 的指针 类型， av_read_packet 函数将读到的包添加至 raw_packet_buffer 链表末尾。

## Decoder/Encoder 模块

### 概述
编解码模块主要包含的数据结构为：AVCodec、AVCodecContext 每一个解码类型都 会有自己的 Codec 静态对像， Codec 的 int priv_data_size 记录该解码器上下文的结构 大 小 ， 如 MsrleContext 。 这 些 都 是 编 译 时 确 定 的 ， 程 序 运 行 时 通 过 avcodec_register_all()将所有的解码器注册成一个链表。在 av_open_input_stream() 函数中调用 AVInputFormat 的 read_header()中读文件头信息时， 会读出数据流的 CodecID， 即确定了他的解码器 Codec。
在 main()函数中除了解析传入参数并初始化 demuxer 与 muxer 的 parse_options( ) 函数以外， 其他的功能都是在 av_encode( )函数里完成的。 在 libavcodec\utils.c 中 有 如 下 二 个 函 数 :AVCodec *avcodec_find_encoder(enum CodecID id) 和 AVCodec *avcodec_find_decoder(enum CodecID id)他们的功能就是根据传入的 CodecID， 找到 匹配的 encoder 和 decoder。在 av_encode( )函数的开头，首先初始化各个 AVInputStream 和 AVOutputStream，然后分别调用上述二个函数，并将匹配上的 encoder 与 decoder 分 别保存在:

`AVInputStream->AVStream *st->AVCodecContext *codec->struct AVCodec *codec `
与

`AVOutputStream->AVStream *st->AVCodecContext *codec->struct AVCodec *codec`

变量。

### 相关数据结构的初始化
AVCodecContext 结构

AVCodecContext 保存 AVCodec 指针和与 codec 相关数据，如 video 的 width、height， audio 的 sample rate 等。

AVCodecContext 中的 codec_type， codec_id 二个变量对于 encoder/decoder 的匹 配来说， 最为重要。
```
enum CodecType codec_type; /* see CODEC_TYPE_xxx */
enum CodecID codec_id; /* see CODEC_ID_xxx */
```

如上所示， codec_type 保存的是 CODEC_TYPE_VIDEO， CODEC_TYPE_AUDIO 等媒体类 型， codec_id 保存的是 CODEC_ID_FLV1， CODEC_ID_VP6F 等编码方式。

以支持 flv 格式为例， 在前述的 av_open_input_file(…… ) 函数中， 匹配到正确 的 AVInputFormat demuxer 后，通过 av_open_input_stream( )函数中调用 AVInputFormat 的 read_header 接口来执行 flvdec.c 中的 flv_read_header( )函数。flv_read_header( ) 函数内， 根据文件头中的数据， 创建相应的视频或音频 AVStream， 并设置 AVStream 中 AVCodecContext 的正确的 codec_type 值。codec_id 值是在解码过程。flv_read_packet( ) 函数执行时根据每一个 packet 头中的数据来设置的。

以 avidec 为例 有如下初始化，我们主要知道的就是 code_id 和 code_type 该字段关 联具体的解码器， 和解码类型（音视频或 subtitle）

## 其他重要数据结构的初始化
### AVStream
AVStream 结构保存与数据流相关的编解码器，数据段等信息

### AVInputStream/ AVOutputStream
根据输入和输出流的不同， 前述的 AVStream 结构都是封装在 AVInputStream 和 AVOutputStream 结构中， 在 av_encode( )函数中使用。 AVInputStream 中还保存的有与 时间有关的信息。 AVOutputStream 中还保存有与音视频同步等相关的信息。

### AVPacket
