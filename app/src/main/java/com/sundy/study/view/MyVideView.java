package com.sundy.study.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.VideoView;

import com.sundy.ffmpeg.VideoPlayer;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-25 19:26
 * 描述：
 */
public class MyVideView extends SurfaceView {
    VideoPlayer mVideoPlayer;
    Surface mSurface;
    public MyVideView(Context context) {
        this(context,null);
    }

    public MyVideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mVideoPlayer=new VideoPlayer();
        getHolder().setFormat(PixelFormat.RGBA_8888);
        mSurface=getHolder().getSurface();
    }


    public void startPlay(String videoPath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mVideoPlayer.play(videoPath,mSurface);
            }
        }).start();
    }
}
