package com.sundy.study;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.sundy.common.base.BaseActivity;
import com.sundy.ffmpeg.PushStream;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-21 16:22
 * 描述：使用ffmpeg实现Android端推流
 * 若果测试推流的话 需要自己搭建本地推流服务器
 */
public class PushStreamActivity extends BaseActivity {
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/input.flv";
    private static final String LIVE_URL = "rtmp://192.168.1.6/live1/room";
    PushStream mPushStream;
    @BindView(R2.id.input_url)
    EditText inputUrl;
    @BindView(R2.id.output_url)
    EditText outputUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.sy_push_stream_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mPushStream = new PushStream();
        inputUrl.setText(FILE_PATH);
        outputUrl.setText(LIVE_URL);
    }

    @OnClick(R2.id.button_push_start)
    public void onClickEvent() {
        String filePath = inputUrl.getText().toString();
        String liveUrl = outputUrl.getText().toString();
        if (!TextUtils.isEmpty(filePath) && !TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            //判断文件是否存在
            if (file.exists()) {
                //开启子线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //开始推流
                        mPushStream.stream(filePath, liveUrl);
                    }
                }).start();
            } else {
                Toast.makeText(mContext, "没有找到文件", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
