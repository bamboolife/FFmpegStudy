package com.sundy.ffmpeg;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-22 17:52
 * 描述：
 */
public class PushStream {
    static {
        System.loadLibrary("ffmpeg-lib");
    }

    /**
     * 视频推流
     * @param inputurl
     * @param outputurl
     * @return
     */
    public native int stream(String inputurl, String outputurl);
}
