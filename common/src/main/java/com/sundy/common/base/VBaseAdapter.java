package com.sundy.common.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.sundy.common.listener.ItemListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @param <T>
 */
public abstract class VBaseAdapter<T> extends DelegateAdapter.Adapter<BaseViewHolder> {
    //上下文
    public Context mContext;
    //布局文件资源ID
    private int mLayoutResId;
    private VirtualLayoutManager.LayoutParams mLayoutParams;
    //数据源
    private List<T> mDatas;
    //布局管理器
    private LayoutHelper mLayoutHelper;
    protected LayoutInflater mLayoutInflater;

    //回调监听
    private ItemListener mListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    public VBaseAdapter() {
    }
    public VBaseAdapter(int mResLayout, LayoutHelper layoutHelper) {
        if (mResLayout == 0) {
            throw new RuntimeException("res is null,please check your params !");
        }
        this.mLayoutResId = mResLayout;
        this.mLayoutHelper = layoutHelper;
        //this.mLayoutParams = new VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        // ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public VBaseAdapter(List<T> mDatas, int mResLayout, LayoutHelper layoutHelper) {
        if (mResLayout == 0) {
            throw new RuntimeException("res is null,please check your params !");
        }
        this.mLayoutResId = mResLayout;
        this.mLayoutHelper = layoutHelper;
        this.mDatas = mDatas;
        //this.mLayoutParams = new VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        // ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public VBaseAdapter(T mItem, int mResLayout, LayoutHelper layoutHelper) {
        if (mResLayout == 0) {
            throw new RuntimeException("res is null,please check your params !");
        }
        this.mLayoutResId = mResLayout;
        this.mLayoutHelper = layoutHelper;
        if (mDatas == null) {
            this.mDatas = new ArrayList<>();
        }
        this.mDatas.add(mItem);
        //this.mLayoutParams = new VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        // ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * <br/> 方法名称: VBaseAdapter
     * <br/> 方法详述: 设置数据源
     * <br/> 参数: mDatas，数据源
     * <br/> 返回值:  VBaseAdapter
     */
    public VBaseAdapter setData(List<T> mDatas) {
        if (mDatas == null) {
            this.mDatas = new ArrayList<>();
        }
        this.mDatas = mDatas;
        notifyDataSetChanged();
        return this;
    }

    public VBaseAdapter addData(List<T> newDatas) {
        this.mDatas.addAll(newDatas);
        notifyDataSetChanged();
        return this;
    }

    /**
     * <br/> 方法名称: setItem
     * <br/> 方法详述: 设置单个数据源
     * <br/> 参数: mItem，单个数据源
     * <br/> 返回值:  VBaseAdapter
     */
    public VBaseAdapter setItem(T mItem) {
        if (mDatas == null) {
            this.mDatas = new ArrayList<>();
        }
        this.mDatas.add(mItem);
        notifyDataSetChanged();
        return this;
    }

    /**
     * <br/> 方法名称: setLayout
     * <br/> 方法详述: 设置布局资源ID
     * <br/> 参数: mResLayout, 布局资源ID
     * <br/> 返回值:  VBaseAdapter
     */
    public VBaseAdapter setLayout(int mResLayout) {
        if (mResLayout == 0) {
            throw new RuntimeException("res is null,please check your params !");
        }
        this.mLayoutResId = mResLayout;
        return this;
    }

    /**
     * <br/> 方法名称: setLayoutHelper
     * <br/> 方法详述: 设置布局管理器
     * <br/> 参数: layoutHelper，管理器
     * <br/> 返回值:  VBaseAdapter
     */
    public VBaseAdapter setLayoutHelper(LayoutHelper layoutHelper) {
        this.mLayoutHelper = layoutHelper;
        return this;
    }

    /**
     * <br/> 方法名称: setHolder
     * <br/> 方法详述: 设置holder
     * <br/> 参数: mClazz,集成VBaseHolder的holder
     * <br/> 返回值:  VBaseAdapter
     */
//    public VBaseAdapter setHolder(Class<? extends VBaseHolder> mClazz) {
//        if (mClazz == null) {
//            throw new RuntimeException("clazz is null,please check your params !");
//        }
//        this.mClazz = mClazz;
//        return this;
//    }

    /**
     * <br/> 方法名称: setListener
     * <br/> 方法详述: 传入监听，方便回调
     * <br/> 参数: listener
     * <br/> 返回值:  VBaseAdapter
     */
//    public VBaseAdapter setListener(ItemListener listener) {
//        this.mListener = listener;
//        return this;
//    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return mLayoutHelper;
    }

    public HashMap<Integer, Object> tags = new HashMap<>();

    /**
     * <br/> 方法名称: setTag
     * <br/> 方法详述: 设置mObject
     * <br/> 参数: mObject
     * <br/> 返回值:  VBaseAdapter
     */
    public VBaseAdapter setTag(int tag, Object mObject) {
        if (mObject != null) {
            tags.put(tag, mObject);
        }
        return this;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        BaseViewHolder baseViewHolder = onCreateDefViewHolder(parent, viewType);
        bindViewClickListener(baseViewHolder);
        baseViewHolder.setAdapter(this);
        return baseViewHolder;
    }

    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, mLayoutResId);
    }

    protected BaseViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        return createBaseViewHolder(getItemView(layoutResId, parent));
    }

    protected View getItemView(int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    /**
     * if you want to use subclass of BaseViewHolder in the adapter,
     * you must override the method to create new ViewHolder.
     *
     * @param view view
     * @return new ViewHolder
     */
    protected BaseViewHolder createBaseViewHolder(View view) {
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int positions) {
        convert(holder, mDatas.get(positions), positions);

    }

    private void bindViewClickListener(final BaseViewHolder baseViewHolder) {
        if (baseViewHolder == null) {
            return;
        }
        final View view = baseViewHolder.getConvertView();
        if (view == null) {
            return;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null && baseViewHolder != null) {

                    getOnItemClickListener().onItemClick(VBaseAdapter.this, v, baseViewHolder.getLayoutPosition() );
                }

            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getOnItemLongClickListener() != null && baseViewHolder != null) {
                    return getOnItemLongClickListener().onItemLongClick(VBaseAdapter.this, v, baseViewHolder.getLayoutPosition() );
                }
                return false;

            }
        });
    }

    protected abstract void convert(BaseViewHolder helper, T item, int position);

//    @Override
//    public void onBindViewHolder(@NonNull VBaseHolder<T> holder, int position) {
//        holder.setListener(mListener);
//        holder.setContext(mContext);
//        holder.setData(position, mDatas.get(position));
//    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this RecyclerView has
         * been clicked.
         *
         * @param adapter  the adpater
         * @param view     The itemView within the RecyclerView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         */
        void onItemClick(VBaseAdapter adapter, View view, int position);
    }
    public interface OnItemLongClickListener {
        /**
         * callback method to be invoked when an item in this view has been
         * click and held
         *
         * @param adapter  the adpater
         * @param view     The view whihin the RecyclerView that was clicked and held.
         * @param position The position of the view int the adapter
         * @return true if the callback consumed the long click ,false otherwise
         */
        boolean onItemLongClick(VBaseAdapter adapter, View view, int position);
    }

    /**
     * Register a callback to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }
    public final OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    /**
     * @return The callback to be invoked with an item in this RecyclerView has
     * been clicked and held, or null id no callback as been set.
     */
    public final OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }
}
