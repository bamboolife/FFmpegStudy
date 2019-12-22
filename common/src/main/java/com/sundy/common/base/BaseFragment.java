package com.sundy.common.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 项目名称：CommonLibrary
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-11-15 21:52
 * 描述：Fragment基类
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected Activity mActivity;
    /**
     * 当前视图
     */
    protected View mView;
    Unbinder mUnbinder;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ARouter.getInstance().inject(this);
        mContext=getContext();
        mActivity=getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
            return mView;
        }
        mView = inflater.inflate(getLayoutId(), container, false);
        mUnbinder = ButterKnife.bind(this, mView);
        initData();
        initViews(savedInstanceState);
        setListeners();
        loadData();

        /**
         * 点击其他部位隐藏软键盘
         */
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getWindowToken() != null) {
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
        return mView;
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
    protected void setListeners() { }
    /**
     * 载入网络数据
     * 没有网络加载则不重载
     */
    protected void loadData(){ }

    /**
     * 销毁对应注册的资源
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder!=null){
            mUnbinder.unbind();
        }
    }
}
