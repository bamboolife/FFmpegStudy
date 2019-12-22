package com.sundy.study;

import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sundy.common.base.BaseActivity;
import com.sundy.study.adapter.OperationAdapter;
import com.sundy.study.entity.VideoOperationEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 视频转码
 */
public class VideoTranscoderActivity extends BaseActivity {


    @BindView(R.id.vRecyclerView)
    RecyclerView vRecyclerView;
   OperationAdapter mAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.sy_video_transcoder_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
         mAdapter=new OperationAdapter(getDatas());
         vRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
         vRecyclerView.setAdapter(mAdapter);
    }

    private List<VideoOperationEntity> getDatas() {
        List<VideoOperationEntity> items=new ArrayList<>();
        items.add(new VideoOperationEntity("视频解码",VideoCodecActivity.class));
        items.add(new VideoOperationEntity("本地推流",PushStreamActivity.class));
        items.add(new VideoOperationEntity("视频转码",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("视频剪切",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("视频截图",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("视频水印",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("视频转gif",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("图片合成视频",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("画面拼接",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("视频降噪",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("视频转图片",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("视频画中画",FFmpegInfoActivity.class));
        items.add(new VideoOperationEntity("MOOV前移",FFmpegInfoActivity.class));

        return items;
    }

    @Override
    public void setListeners() {
        super.setListeners();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
               VideoOperationEntity item= (VideoOperationEntity) adapter.getItem(position);
                Intent intent=new Intent(mContext,item.mClass);
                startActivity(intent);
            }
        });
    }
}
