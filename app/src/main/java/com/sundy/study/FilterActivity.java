package com.sundy.study;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import com.sundy.common.base.BaseActivity;
import com.sundy.study.view.MyVideView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-21 16:22
 * 描述：FFmpeg滤镜
 */
public class FilterActivity extends BaseActivity {
    String videoPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/test.mp4";
    @BindView(R2.id.videoView)
    MyVideView videoView;

    @Override
    protected int getLayoutId() {
        return R.layout.sy_filter_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }
    @OnClick(R2.id.start)
    public void onClickEvent(){
       videoView.startPlay(videoPath);

    }

}


