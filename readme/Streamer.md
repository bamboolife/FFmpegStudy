# FFmpeg RTMP推流

## 在开发前一定要先了解下结构体和结构之间的关系

### 结构体关系
![image](https://github.com/bamboolife/FFmpegStudy/blob/master/images/struct.png)

### 结构体
- `AVFormatContext` AVFormatContext是格式封装的上下文对象。下面是比较常用的成员变量
  - `AVIOContext *pb`：用来合成音频和视频，或者分解的AVIOContext
  - `unsigned int nb_streams`：视音频流的个数
  - `AVStream **streams`：视音频流
  - `char filename[1024]`：文件名
  - `AVDictionary *metadata`：存储视频元信息的metadata对象。

- AVDictionaryEntry
每一条元数据分为key和value两个属性。
```
typedef struct AVDictionaryEntry {
    char *key;
    char *value;
} AVDictionaryEntry;
```
可以根据下面代码。取出这些数据
```
AVFormatContext *fmt_ctx = NULL;
    AVDictionaryEntry *tag = NULL;
    int ret;

    if ((ret = avformat_open_input(&fmt_ctx, argv[1], NULL, NULL)))
        return ret;

    while ((tag = av_dict_get(fmt_ctx->metadata, "", tag, AV_DICT_IGNORE_SUFFIX)))
        printf("%s=%s\n", tag->key, tag->value);

    avformat_close_input(&fmt_ctx);
```
- AVRational
表示媒体信息的一些分数，是分母和分子的结构。计算过程中，会多次使用这样的数据结构
```
typedef struct AVRational{
    int num; ///< Numerator
    int den; ///< Denominator
} AVRational;
```
- AVPacket  AVPacket是存储压缩编码数据相关信息的结构体。
  - uint8_t *data：压缩编码的数据。
  - int size：data的大小
  - int64_t pts：显示时间戳
  - int64_t dts：解码时间戳
  - int stream_index：标识该AVPacket所属的视频/音频流。
  
### FFmpeg推流流程
![image](https://github.com/bamboolife/FFmpegStudy/blob/master/images/stream.png)

### 方法结构流向图
![image](https://github.com/bamboolife/FFmpegStudy/blob/master/images/stream2.png)




