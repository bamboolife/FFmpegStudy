package com.sundy.common.utils;

import android.graphics.drawable.GradientDrawable;

/**
 * 项目名称：CommonLibrary
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-11-29 11:29
 * 描述：Drawable的相关操作
 */
public class DrawableUtils {

   public GradientDrawable getGradient(GradientDrawable.Orientation orientation,int startClolor,int endColor){
       GradientDrawable gd=new GradientDrawable(orientation,new int[]{startClolor,endColor});
       return gd;
   }
}
