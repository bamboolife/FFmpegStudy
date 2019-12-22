package com.sundy.common.base;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 项目名称：CommonLibrary
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-11-15 21:52
 * 描述：AppCompatActivity基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    protected Activity mActivity;
    private Unbinder mUnbinder=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        mContext=getApplicationContext();
        mActivity=this;
        initData();
        setContentView(getLayoutId());
        mUnbinder= ButterKnife.bind(this);
        initViews(savedInstanceState);
        setListeners();
        loadData();
    }

    /**
     * 程序开始数据的初始化
     */
    protected void initData(){ }
    /**
     * 视图id
     * @return
     */
    protected abstract int getLayoutId();
    /**
     * 视图初始化
     * @param savedInstanceState
     */
    protected abstract void initViews(Bundle savedInstanceState);
    /**
     * 控件监听
     */
    public void setListeners() { }
    /**
     * 载入网络数据
     * 没有网络加载则不重载
     */
    protected void loadData(){ }

    /**
     * 销毁对应注册的资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder!=null){
            mUnbinder.unbind();
        }
    }
}
