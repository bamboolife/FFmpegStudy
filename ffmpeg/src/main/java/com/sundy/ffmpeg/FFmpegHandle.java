package com.sundy.ffmpeg;

/**
 * Author : eric
 * CreateDate : 2017/11/1  15:32
 * Email : ericli_wang@163.com
 * Version : 2.0
 * Desc :
 * Modified :
 */

public class FFmpegHandle {
    private static FFmpegHandle mInstance;

    public synchronized static FFmpegHandle getInstance() {
        if (mInstance == null) {
            mInstance = new FFmpegHandle();
        }
        return mInstance;
    }

    static {
        System.loadLibrary("ffmpeg-lib");
    }

    public native int setCallback(PushCallback pushCallback);


    public native int pushRtmpFile(String path);

    public native int initVideo(String url,int jwidth,int jheight);

    public native int onFrameCallback(byte[] buffer);

    public native int sendH264(byte[] buffer,int len);

    public native int initVideo2(String url);

    public native int close();
}
