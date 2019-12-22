package com.sundy.ffmpeg;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-22 19:29
 * 描述：
 */
public class VideoCodec {
    static {
        System.loadLibrary("ffmpeg-lib");
    }
    /**
     * 视频解码
     * @param inputurl
     * @param outputurl
     * @return
     */
    public native int decode(String inputurl, String outputurl);
}
