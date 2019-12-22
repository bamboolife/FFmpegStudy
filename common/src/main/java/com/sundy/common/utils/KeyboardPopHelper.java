package com.sundy.common.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.sundy.common.view.XEditText;

/**
 * 项目名称：CommonLibrary
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-11-24 17:52
 * 描述：键盘遮挡工具类
 */
public class KeyboardPopHelper implements ViewTreeObserver.OnGlobalLayoutListener {
    private Activity mActivity;
    private XEditText[] mTargetEditTexts;
    private View mFocusView;
    private View mRootView;
    private int mTargetOffset = 0;
    private boolean mIsOffsetFixed = false; // 是否使用固定偏移
    private boolean mIsMonitorFocusViewChanged = false; // 是否监控focusView高度的变化
    private final int mMaxAnimDuration = 250;
    private int mTargetMarginBottom = 2; // targetView的bottom margin
    private int mKeyboardHeight = 0, mKeyBoardTop = 0;
    private ObjectAnimator mAnimator;
    private OnLayoutListener mOnLayoutListener;

    private int mFocusViewHeight = 0;

    public interface OnLayoutListener {
        void onLayoutChanged(float offset);
    }

    private KeyboardPopHelper(Activity activity) {
        this.mActivity = activity;
    }

    public static KeyboardPopHelper instance(Activity activity) {
        return new KeyboardPopHelper(activity);
    }

    public KeyboardPopHelper bindEditText(XEditText... views) {
        mTargetEditTexts = views;
        return this;
    }

    public KeyboardPopHelper bindRootView(View view) {
        mRootView = view;
        return this;
    }

    public KeyboardPopHelper setOffset(int offset) {
        mIsOffsetFixed = true;
        mTargetOffset = (int) (Resources.getSystem().getDisplayMetrics().density * offset);
        return this;
    }

    public KeyboardPopHelper setBottomMargin(int bottomMargin) {
        this.mTargetMarginBottom = bottomMargin;
        return this;
    }

    public KeyboardPopHelper setMonitorFocusSizeChanged(boolean changed) {
        this.mIsMonitorFocusViewChanged = changed;
        return this;
    }

    public KeyboardPopHelper monitor() {
        if (null != mTargetEditTexts && mTargetEditTexts.length > 0) {
            for (EditText view : mTargetEditTexts) {
                view.setFocusableInTouchMode(true);
                view.setFocusable(true);
                // view.setOnFocusChangeListener(this);
            }
        }
        if (null == mRootView) mRootView = mActivity.findViewById(android.R.id.content);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        return this;
    }

    public KeyboardPopHelper addLayoutListener(OnLayoutListener layoutListener) {
        this.mOnLayoutListener = layoutListener;
        return this;
    }

    @Override
    public void onGlobalLayout() {
        // 如果绑定了EditText，并且全部失去了焦点，则什么都不做
        if (null != mTargetEditTexts && mRootView == mFocusView) {
            return;
        }
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        // 底部bar
        int navigationBarHeight = getNavigationBarHeight(mActivity);
        int screenBottom = r.bottom;
        int change = screenHeight - navigationBarHeight - screenBottom;
        if (change > 0) {
            // 记录键盘位置
            mKeyBoardTop = screenBottom;
            mKeyboardHeight = change;
        }
        // focusView的尺寸发生变化时，也会进入这个方法里，导致偏移量的计算紊乱，因此需要处理focusView的尺寸变化量
        // 这里有一个bug，如果布局里有scrollview时，会出现紊乱，布局里有recyclerview时不会出现紊乱，
        // 因此再加一个参数控制是否监控尺寸的变化
        int focusViewHeightChanged = 0;
        if (mIsMonitorFocusViewChanged) {
            if (null != mFocusView) {
                int currentHeight = mFocusView.getHeight();
                if (mFocusViewHeight == 0) mFocusViewHeight = currentHeight; // 初始化完成
                if (currentHeight != mFocusViewHeight) {
                    focusViewHeightChanged = mFocusViewHeight - currentHeight;
                    mFocusViewHeight = currentHeight;
                }
            }
        }
        reLayoutView(screenBottom, change, focusViewHeightChanged);
    }

//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        if (hasFocus) {
//            // 获得焦点，布局弹起
//            mFocusView = v;
//            if (mKeyboardHeight != 0 && isKeyboardShow()) {
//                // 焦点切换时，如果键盘是弹出的，则需要需要加上mRootView已经偏移的距离
//                int bottom = (int) (mKeyBoardTop + mRootView.getTranslationY());
//                reLayoutView(bottom, mKeyboardHeight, 0);
//            }
//        } else if (null == getCurrentFocus()) {
//            // 全部失去焦点，布局回位
//            mFocusView = mRootView;
//            if (mRootView.getTranslationY() < 0)
//                reLayoutView(0, 0, 0);
//        }
//    }

    /**
     * 处理布局的变化
     *
     * @param screenBottom
     * @param layoutChange 键盘高度
     */
    private void reLayoutView(final int screenBottom, final int layoutChange, int focusHeightChanged) {
        if (layoutChange > 0 && !mIsOffsetFixed) {
            // 键盘弹出状态
            if (null == mFocusView)
                mTargetOffset = 0;
            else {
                Log.d("KeyboardPopHelper", "focusHeightChanged " + focusHeightChanged);
                if (focusHeightChanged != 0) {
                    // 如果focusView的高度变化了，直接累加高度即可，防止重新focusView的bottom时出现的紊乱
                    mTargetOffset += focusHeightChanged;
                } else {
                    // focusView的高度未变化，实时更新计算focusView的bottom，再计算偏移量
                    int targetBottom = computeTargetViewBottom(mFocusView) + mTargetMarginBottom;
                    Log.d("KeyboardPopHelper", "show targetBottom " + targetBottom);
                    // mFocusView 位置发生变化时，重新计算偏移
                    if (targetBottom != screenBottom)
                        mTargetOffset = screenBottom - targetBottom;
                }
            }
            mTargetOffset = Math.min(0, mTargetOffset);
        }
        if (mTargetOffset == 0) return;
        if (mRootView.getTranslationY() == 0 && layoutChange == 0) return; // 键盘已经收起
        if (mRootView.getTranslationY() == mTargetOffset && layoutChange > 0) return; // 键盘已经弹出
        if (null != mAnimator && mAnimator.isRunning()) mAnimator.cancel();
        float currentOffset = mRootView.getTranslationY();
        final float finalTransY;
        int duration;
        if (layoutChange <= 0) {
            // 键盘收起
            finalTransY = 0;
            duration = (int) (currentOffset / mTargetOffset * mMaxAnimDuration);
        } else {
            // 键盘弹出
            finalTransY = mTargetOffset;
            duration = (int) ((mTargetOffset - currentOffset) / mTargetOffset * mMaxAnimDuration);
        }
        if (duration <= 0 || duration > mMaxAnimDuration) duration = mMaxAnimDuration;

        mAnimator = ObjectAnimator.ofFloat(mRootView, "translationY",
                currentOffset, finalTransY);
        mAnimator.setDuration(duration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float current = (float) animation.getAnimatedValue();
                mRootView.setTranslationY(current);
                if (null != mOnLayoutListener)
                    mOnLayoutListener.onLayoutChanged(-current / mTargetOffset);
            }
        });
        mAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        mAnimator.start();
    }

    private boolean isKeyboardShow() {
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int navigationBarHeight = getNavigationBarHeight(mActivity);
        int change = screenHeight - navigationBarHeight - r.bottom;
        return change > 0;
    }

    /**
     * 寻找获得焦点的 EditText
     *
     * @return
     */
    private EditText getCurrentFocus() {
        if (null != mTargetEditTexts || mTargetEditTexts.length > 0) {
            for (EditText view : mTargetEditTexts) {
                if (view.hasFocus()) return view;
            }
        }
        return null;
    }

    /**
     * 计算 mTargetEditText在屏幕中的位置
     *
     * @return
     */
    private int computeTargetViewBottom(View target) {
        int[] location = new int[2];
        target.getLocationOnScreen(location);
        int statusBarHeight = getStatusBarHeight(mActivity);
        int titleBarHeight = 0;
        // actionbar height
        ViewGroup content = mActivity.findViewById(android.R.id.content);
        titleBarHeight = content.getTop();
        // statusBar height compat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
            boolean isTranslucentStatus = (params.flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0;
            boolean isFitSystemWindows = true;
            if (null != content.getChildAt(0))
                isFitSystemWindows = content.getChildAt(0).getFitsSystemWindows();

            if (isTranslucentStatus && !isFitSystemWindows)
                statusBarHeight = 0;
        }
        Log.d("KeyboardPopHelper", "location[1] " + location[1]);
        return location[1] - (titleBarHeight + statusBarHeight) + target.getHeight();
    }

    private int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private int getNavigationBarHeight(Context context) {
        if (!checkDeviceHasNavigationBar()) return 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    @SuppressLint("NewApi")
    private boolean checkDeviceHasNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = mActivity.getWindow().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
            boolean menu = ViewConfiguration.get(mActivity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !menu && !back;
        }
    }
}
