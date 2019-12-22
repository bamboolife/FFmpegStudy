package com.sundy.study;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sundy.common.base.BaseActivity;
import com.sundy.study.adapter.FunctionAdapter;
import com.sundy.study.entity.FunctionEntity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.permission.runtime.PermissionDef;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {


    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    FunctionAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void initViews(Bundle savedInstanceState) {
        requestPermission(Permission.Group.STORAGE);
        requestPermission(Permission.RECORD_AUDIO);
        mAdapter=new FunctionAdapter(getDatas());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.HORIZONTAL));
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<FunctionEntity> getDatas() {
        List<FunctionEntity> items=new ArrayList<>();
        items.add(new FunctionEntity("FFmpeg库信息打印",FFmpegInfoActivity.class));
        items.add(new FunctionEntity("视频处理",VideoTranscoderActivity.class));
        return items;
    }

    @Override
    public void setListeners() {
        super.setListeners();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FunctionEntity entity= (FunctionEntity) adapter.getItem(position);
                Intent intent=new Intent(mContext,entity.mClass);
                startActivity(intent);
            }
        });
    }

    /**
     * Request permissions.
     */
    private void requestPermission(@PermissionDef String... permissions) {
        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                // .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {

                    }
                })
                .start();
    }

}
