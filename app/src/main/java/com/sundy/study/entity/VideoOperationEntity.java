package com.sundy.study.entity;

/**
 * 项目名称：FFmpegStudy
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-12-21 19:07
 * 描述：
 */
public class VideoOperationEntity {
    public String  title;
    public Class mClass;

    public VideoOperationEntity(String title, Class aClass) {
        this.title = title;
        mClass = aClass;
    }
}
