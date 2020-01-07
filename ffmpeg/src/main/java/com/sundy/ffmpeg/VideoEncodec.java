package com.sundy.ffmpeg;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-29 16:46
 * 描述：
 */
public class VideoEncodec {
    {
        System.loadLibrary("ffmpeg-lib");
    }

    public native int encodec(String inputurl, String outputurl);
}
