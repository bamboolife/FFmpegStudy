package com.sundy.ffmpeg;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-22 09:02
 * 描述：
 */
public class VideoPlayer {
    {
        System.loadLibrary("ffmpeg-lib");
    }
    public native int play(String filePath, Object surface);
    public native void setPlayRate(float playRate);

    public native int filter(String filePath, Object surface, String filterType);
    public native void again();
    public native void release();
    public native void playAudio(boolean play);




}
