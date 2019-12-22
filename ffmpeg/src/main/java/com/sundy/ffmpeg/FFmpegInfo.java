package com.sundy.ffmpeg;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-12 21:27
 * 描述：FFmpeg库
 */
public class FFmpegInfo {
    static {
        System.loadLibrary("ffmpeg-lib");
    }

    /**
     * FFmpeg类库支持的协议
     * @return
     */
    public native String urlprotocolinfo();

    /**
     * FFmpeg类库支持的封装格式
     * @return
     */
    public native String avformatinfo();

    /**
     * FFmpeg类库支持的编解码器
     * @return
     */
    public native String avcodecinfo();

    /**
     * FFmpeg类库支持的滤镜
     * @return
     */
    public native String avfilterinfo();

    /**
     * FFmpeg类库的配置信息
     * @return
     */
    public native String configurationinfo();

}
