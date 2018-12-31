package com.chen.fy.appmanager.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.chen.fy.appmanager.entity.AppInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工具类
 */
public class Utils {

    public static List<AppInfo> getAppList(Context context){
        //获得返回值集合
        List<AppInfo> list = new ArrayList<AppInfo>();
        //实例化包管理器
        PackageManager pm = context.getPackageManager();
        //获取所有已经安装的应用信息
        List<PackageInfo> plist= pm.getInstalledPackages(0);
        //遍历集合
        for(int i=0;i<plist.size();i++){
            //拿到PackageInfo元素
            PackageInfo packageInfo = plist.get(i);
            //填充实体类 除去第三方应用,即不可更新不可卸载的系统应用
            if(isThirdPratyApp(packageInfo.applicationInfo) &&
                    !packageInfo.packageName.equals(context.getPackageName())) {//除去自己本身
                AppInfo appInfo = new AppInfo();
                appInfo.packageName = packageInfo.packageName;
                appInfo.versionName = packageInfo.versionName;
                appInfo.versionCode = packageInfo.versionCode;
                appInfo.firstInstallTime = packageInfo.firstInstallTime;
                appInfo.lastUpdateTime = packageInfo.lastUpdateTime;
                //获取应用名
                appInfo.appName = (String) packageInfo.applicationInfo.loadLabel(pm);
                //获取图标
                appInfo.icon = packageInfo.applicationInfo.loadIcon(pm);
                //计算程序大小
                String dir = packageInfo.applicationInfo.publicSourceDir;  //找出安装包绝对路径
                long byteSize = new File(dir).length();   //得出程序的字节大小
                appInfo.byteSize = byteSize;   //字节大小
                appInfo.size = gteSize(byteSize);   //转化为兆后的大小
                list.add(appInfo);
            }
        }

        return  list;
    }

    /**
     * 把字节大小转化为兆大小
     */
    public static String gteSize(long size){
        // 转化                         表示小数点后两位            转化为兆的计算
        return new DecimalFormat("0.##").format((size*1.0)/(1024*1024));
    }

    /**
     * 转化日期格式
     */
    public static String getTime(long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 点击行时运行程序
     */
    public static boolean openPackage(Context context,String packageName){
        //获取一个代表程序包的信息,并可以打开它
        try {
            Intent intent =
                    context.getPackageManager().getLaunchIntentForPackage(packageName);
            if(intent != null){
                //在一个新线程中启动,使返回时仍在启动前界面
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return  true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 卸载App
     * @param requestCode 请求码,用以判断返回的信息是否是原来的信息
     */
    public static void uninstallApp(Activity context, String packageName,
                                    int requestCode){
                            //表示打开的是一个包地址
        Uri uri = Uri.parse("package:"+packageName);  //解析包的地址(Uri是一个专门描述地址的类
        Intent intent = new Intent(Intent.ACTION_DELETE,uri);  //发送卸载信息到uri地址即app地址
        context.startActivityForResult(intent,requestCode);  //跳转至卸载界面,并携带有请求码

    }

    /**
     * 判断应用是否是第三方应用
     */
    public static boolean isThirdPratyApp(ApplicationInfo applicationInfo){
        boolean flag = false;
        if((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
            flag = true;    //可更新系统应用
        }else if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
            flag = true;    //非系统应用
        }

        return flag;
    }

    /**
     * 搜索结果数据
     */
    public static List<AppInfo> searchResult(List<AppInfo> list,String search){
        List<AppInfo> result = new ArrayList<AppInfo>();
        for(int i=0;i<list.size();i++){
            AppInfo appInfo = list.get(i);
            if(appInfo.appName.toLowerCase().contains(search.toLowerCase())){ //比较
                result.add(appInfo);
            }
        }
        return result;
    }

    /**
     * 搜索时显示高亮
     */
    public static SpannableStringBuilder highLightText(String str,String key){
        int start = str.toLowerCase().indexOf(key.toLowerCase());
        int end = start+key.length();

        SpannableStringBuilder sb = new SpannableStringBuilder(str);
        sb.setSpan(
                new ForegroundColorSpan(Color.RED), //设置高亮的颜色
                start,   //起始坐标
                end,     //终止坐标
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE   //旗帜  一般不用改变
        );
        return sb;
    }

}
