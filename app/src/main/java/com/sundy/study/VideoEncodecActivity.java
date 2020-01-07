package com.sundy.study;


import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;

import com.sundy.common.base.BaseActivity;
import com.sundy.ffmpeg.VideoEncodec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoEncodecActivity extends BaseActivity {
    String outPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/mytest.mp4";
    String inputPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/test.yuv";
    VideoEncodec mVideoEncodec;
    @BindView(R2.id.input_yuv_url)
    EditText inputYuvUrl;
    @BindView(R2.id.input_yuv_resolution)
    EditText inputYuvResolution;
    @BindView(R2.id.encode_settings)
    EditText encodeSettings;
    @BindView(R2.id.editText4)
    EditText editText4;
    @BindView(R2.id.button)
    Button button;

    @Override
    protected int getLayoutId() {
        return R.layout.sy_video_encodec_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mVideoEncodec = new VideoEncodec();


    }
    @OnClick(R2.id.button)
    public void onClickEvent(){
        mVideoEncodec.encodec(inputPath,outPath);
    }
}
