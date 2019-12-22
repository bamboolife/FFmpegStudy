package com.sundy.common.listener;

import android.view.View;

/**
 * 类名: {@link ItemListener}
 * <br/> 功能描述: recyclerView万能适配器的点击事件
 */
public interface ItemListener<T> {
    void onItemClick(View view, int position, T mData);
}
