package com.sundy.study;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.sundy.common.base.BaseActivity;
import com.sundy.ffmpeg.VideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPlayActivity extends BaseActivity implements SurfaceHolder.Callback{
    String videoPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/test.mp4";

    @BindView(R2.id.surface_filter)
    SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    VideoPlayer mVideoPlayer;
    //surface是否已经创建
    private boolean surfaceCreated;
    //是否正在播放
    private boolean isPlaying;

    @Override
    protected int getLayoutId() {
        return R.layout.sy_video_play_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mVideoPlayer=new VideoPlayer();
       mSurfaceHolder=   mSurfaceView.getHolder();
       mSurfaceHolder.addCallback(this);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceCreated=true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceCreated=false;
    }

    @OnClick(R2.id.btn_play)
    public void onClickEvent(){
        Log.i("log_txt", "run: 1111111111111111");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mVideoPlayer.play(videoPath,mSurfaceHolder.getSurface());

            }
        });

    }
}
