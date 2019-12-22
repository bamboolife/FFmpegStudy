package com.sundy.ffmpeg;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-22 19:14
 * 描述：
 */
public class AudioPlayer {
    static {
        System.loadLibrary("ffmpeg-lib");
    }
    //调用OpenSL ES播放
    public native void stop();
}
