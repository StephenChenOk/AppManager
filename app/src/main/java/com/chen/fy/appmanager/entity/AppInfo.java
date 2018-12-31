package com.chen.fy.appmanager.entity;

import android.graphics.drawable.Drawable;

import com.chen.fy.appmanager.util.Utils;

/**
 *实体类(也叫bean类
 */
public class AppInfo {
    /**
     * 包名
     */
    public String packageName;
    /**
     * 版本名
     */
    public String versionName;
    /**
     * 版本号
     */
    public int versionCode;
    /**
     * 首次安装时间
     */
    public long firstInstallTime;
    /**
     * 更新时间
     */
    public long lastUpdateTime;
    /**
     * 程序名
     */
    public String appName;
    /**
     * 图标
     */
    public Drawable icon;
    /**
     * 字节大小
     */
    public long byteSize;
    /**
     * 大小
     */
    public String size;

}
