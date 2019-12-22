package com.sundy.study;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.sundy.common.base.BaseActivity;
import com.sundy.ffmpeg.FFmpegInfo;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-21 16:22
 * 描述：FFmpeg类库信息打印
 * Protocol: FFmpeg类库支持的协议
 * AVFormat: FFmpeg类库支持的封装格式
 * AVCodec: FFmpeg类库支持的编解码器
 * AVFilter: FFmpeg类库支持的滤镜
 * Configure: FFmpeg类库的配置信息
 */
public class FFmpegInfoActivity extends BaseActivity {

    @BindView(R2.id.text_libinfo)
    TextView textLibinfo;
    FFmpegInfo utils;

    @Override
    protected int getLayoutId() {
        return R.layout.sy_ffmpeg_info_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        utils = new FFmpegInfo();
        textLibinfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        textLibinfo.setText(utils.configurationinfo());
    }

    @OnClick({R2.id.button_urlprotocol, R2.id.button_avformat, R2.id.button_avcodec, R2.id.button_avfilter, R2.id.button_configuration,R2.id.button_clear})
    public void onClickEvent(View v) {
        switch (v.getId()) {
            case R.id.button_urlprotocol:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textLibinfo.setText("");
                        textLibinfo.setText(utils.urlprotocolinfo());
                    }
                });

                break;
            case R.id.button_avformat:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textLibinfo.setText("");
                        textLibinfo.setText(utils.avformatinfo());
                    }
                });

                break;
            case R.id.button_avcodec:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textLibinfo.setText(utils.avcodecinfo());
                    }
                });

                break;
            case R.id.button_avfilter:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textLibinfo.setText(utils.avfilterinfo());
                    }
                });

                break;
            case R.id.button_configuration:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textLibinfo.setText(utils.configurationinfo());
                    }
                });

                break;
            case R.id.button_clear:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textLibinfo.setText("");
                    }
                });
                break;
        }
    }
}
