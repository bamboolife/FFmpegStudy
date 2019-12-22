package com.sundy.study.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sundy.study.R;
import com.sundy.study.entity.VideoOperationEntity;

import java.util.List;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-21 19:08
 * 描述：
 */
public class OperationAdapter extends BaseQuickAdapter<VideoOperationEntity, BaseViewHolder> {
    public OperationAdapter( @Nullable List<VideoOperationEntity> data) {
        super(R.layout.sy_video_item_layout, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, VideoOperationEntity item) {
          helper.setText(R.id.tv_video,item.title);
    }
}
