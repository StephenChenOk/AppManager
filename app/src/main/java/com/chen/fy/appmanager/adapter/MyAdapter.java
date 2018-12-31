package com.chen.fy.appmanager.adapter;
/**
 * 自定义适配器
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.fy.appmanager.MainActivity;
import com.chen.fy.appmanager.R;
import com.chen.fy.appmanager.entity.AppInfo;
import com.chen.fy.appmanager.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {

    //数据
    List<AppInfo> list;
    //反射器
    LayoutInflater inflater;

    public void setiUninstall(IUninstall iUninstall) {
        this.iUninstall = iUninstall;
    }

    //卸载按钮
    IUninstall iUninstall;

    /**
     * 构造器
     * Constext 上下文
     */
    public MyAdapter(Context context) {
        inflater = LayoutInflater.from(context);  //让反射器可以引用上下文
    }

    //传入数据
    public void setList(List<AppInfo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return (list == null)? 0 :list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        //声明ViewHolder对象,用以存放要显示的控件
        ViewHolder holder = null;

        if (view == null) {   //判断缓冲池是否已经有view ,若有则可以直接用,不需要再继续反射
            //用反射器把item布局文件转化为view对象
            view = inflater.inflate(R.layout.item, null);
            //创建ViewHolder对象
            holder = new ViewHolder();
            //从控件中反射获取相应的view对象并存放在ViewHolder类的对象里
            holder.logo = view.findViewById(R.id.logo);
            holder.title = view.findViewById(R.id.title);
            holder.version = view.findViewById(R.id.version);
            holder.size = view.findViewById(R.id.size);
            holder.btn = view.findViewById(R.id.btn);
            //把holder对象一起放到view中,实现优化
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();   //若缓冲池中已经有view则可以直接用holder对象
        }

        //对上面获取的对象进行赋值
        AppInfo appInfo = list.get(i);
        holder.logo.setImageDrawable(appInfo.icon);
        //搜索时设置高亮
        if(MainActivity.KEY == null){
            holder.title.setText(appInfo.appName);
        }else {
            holder.title.setText(Utils.highLightText(appInfo.appName, MainActivity.KEY));
        }
        holder.version.setText("版本:"+appInfo.versionName+" "+appInfo.lastUpdateTime);
        holder.size.setText("大小"+appInfo.size+" M");

        final String packageName = appInfo.packageName; //获取包名
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iUninstall.btn_uninstall(i,packageName);
            }
        });

        return view;
    }

    //创建一个内部类,放着要显示的view控件,通过实例化这个类,把其对象一起放到view中
    public class ViewHolder {
        ImageView logo;
        TextView title;
        TextView version;
        TextView size;
        Button btn;
    }
}