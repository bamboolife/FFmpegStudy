package com.sundy.study.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sundy.study.R;
import com.sundy.study.entity.FunctionEntity;

import java.util.List;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-21 18:14
 * 描述：
 */
public class FunctionAdapter extends BaseQuickAdapter<FunctionEntity, BaseViewHolder> {
    public FunctionAdapter( @Nullable List<FunctionEntity> data) {
        super(R.layout.sy_item_layout, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FunctionEntity item) {
           helper.setText(R.id.tv_title,item.title);
    }
}
