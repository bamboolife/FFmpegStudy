package com.sundy.study;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.sundy.common.base.BaseActivity;
import com.sundy.ffmpeg.FFmpegHandle;
import com.sundy.study.controller.CameraController;
import com.sundy.study.utils.FileUtil;
import com.sundy.study.utils.PhoneUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;

public class CameraPushStreamActivity extends BaseActivity implements SurfaceHolder.Callback {
    private static final String TAG = "log_camera";
    private final int WIDTH = 640;
    @BindView(R2.id.surfaceView)
    SurfaceView surfaceView;
    private int HEIGHT = 480;
    private SurfaceHolder mHolder;
    //    private String url = "rtmp://192.168.31.127/live/test";
    private String url = FileUtil.getMainDir() + "/ffmpeg.flv";
    //采集到每帧数据时间
    long previewTime = 0;
    //每帧开始编码时间
    long encodeTime = 0;
    //采集数量
    int count = 0;
    //编码数量
    int encodeCount = 0;
    //采集数据回调
     private StreamIt mStreamIt;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected int getLayoutId() {
        return R.layout.sy_camera_push_stream_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        init();
    }

    private void init() {

        mStreamIt = new StreamIt();
        CameraController.getInstance().open(1);
        Camera.Parameters params = CameraController.getInstance().getParams();
        params.setPictureFormat(ImageFormat.NV21);
        List<Camera.Size> list = params.getSupportedPictureSizes();
        for (Camera.Size size : list) {
            Log.i(TAG, size.width + " " + size.height);
            if (size.width == WIDTH) {
                HEIGHT = size.height;
                break;
            }
        }
        params.setPictureSize(WIDTH, HEIGHT);
        params.setPreviewSize(WIDTH, HEIGHT);
        params.setPreviewFpsRange(30000, 30000);
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains("continuous-video")) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        CameraController.getInstance().adjustOrientation(this, new CameraController.OnOrientationChangeListener() {
            @Override
            public void onChange(int degree) {
                FrameLayout.LayoutParams lp =
                        (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
                Log.i(TAG, PhoneUtils.getWidth() + " " + PhoneUtils.getHeight());
                if (degree == 90) {
                    lp.height = PhoneUtils.getWidth() * WIDTH / HEIGHT;
                } else {
                    lp.height = PhoneUtils.getWidth() * HEIGHT / WIDTH;
                }
                surfaceView.setLayoutParams(lp);
            }
        });

        CameraController.getInstance().resetParams(params);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        FFmpegHandle.getInstance().initVideo(url, WIDTH, HEIGHT);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mHolder != null) {
            CameraController.getInstance().startPreview(mHolder, mStreamIt);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraController.getInstance().stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraController.getInstance().close();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraController.getInstance().startPreview(mHolder, mStreamIt);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraController.getInstance().stopPreview();
        CameraController.getInstance().close();
    }

    public class StreamIt implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(final byte[] data, Camera camera) {
            long endTime = System.currentTimeMillis();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    encodeTime = System.currentTimeMillis();
                    FFmpegHandle.getInstance().onFrameCallback(data);
                    Log.i(TAG, "编码第:" + (encodeCount++) + "帧，耗时:" + (System.currentTimeMillis() - encodeTime));
                }
            });
            Log.i(TAG,"采集第:" + (++count) + "帧，距上一帧间隔时间:"
                    + (endTime - previewTime) + "  " + Thread.currentThread().getName());
            previewTime = endTime;
        }
    }
}
