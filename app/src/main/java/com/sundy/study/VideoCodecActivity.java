package com.sundy.study;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sundy.common.base.BaseActivity;
import com.sundy.ffmpeg.VideoCodec;

import butterknife.BindView;
import butterknife.OnClick;

public class VideoCodecActivity extends BaseActivity {
    // String folderurl = Environment.getExternalStorageDirectory().getPath() + "/Movies/";
    String folderurl = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/test.mp4";
    String outPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/test.yuv";
    @BindView(R2.id.text_label1)
    TextView textLabel1;
    @BindView(R2.id.input_url)
    EditText inputUrl;
    @BindView(R2.id.text_label2)
    TextView textLabel2;
    @BindView(R2.id.output_url)
    EditText outputUrl;
    @BindView(R2.id.button_start)
    Button buttonStart;
    @BindView(R2.id.text_info)
    TextView textInfo;
    VideoCodec mVideoCodec;

    @Override
    protected int getLayoutId() {
        return R.layout.sy_video_codec_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mVideoCodec = new VideoCodec();
        inputUrl.setText(folderurl);
        outputUrl.setText(outPath);
    }

    @OnClick(R2.id.button_start)
    public void onclickEvent() {
        String inputPath = inputUrl.getText().toString();
        String outputPath = outputUrl.getText().toString();

        mVideoCodec.newdecode(inputPath, outputPath);
    }
}
