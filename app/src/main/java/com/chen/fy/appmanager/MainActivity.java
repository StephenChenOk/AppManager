package com.chen.fy.appmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.fy.appmanager.adapter.IUninstall;
import com.chen.fy.appmanager.adapter.MyAdapter;
import com.chen.fy.appmanager.entity.AppInfo;
import com.chen.fy.appmanager.util.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener,IUninstall, SearchView.OnQueryTextListener {

    ListView listView;
    List<AppInfo> list;
    MyAdapter adapter;

    TextView tv_sort;
    TextView tv_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_listview);

        tv_sort = findViewById(R.id.sort);
        tv_number = findViewById(R.id.number);

        //拿到ListView对象
        listView = (ListView) findViewById(R.id.lv_main);
        //适配器
        adapter = new MyAdapter(this);
        //关联
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        adapter.setiUninstall(this);
        //当第一次进入时就载入数据并更新数据
        updateDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        //获取search的item对象
        MenuItem search = menu.findItem(R.id.search);      //搜索框外的menu对象

        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                //搜索框展开
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                //搜索框收起
                updateDate();   //在搜索框收起的一瞬间重新载入数据并刷新列表
                return true;
            }
        });

        SearchView sv = (SearchView) search.getActionView(); //搜索框对象
        sv.setSubmitButtonEnabled(true);   //搜索框中的提交按钮
        sv.setQueryHint("查找应用名");    //搜索框中的浅色提示文字

        sv.setOnQueryTextListener(this);       //点击提交按钮

        return true;  //消化事件
    }

    /**
     * 排序(按时间.大小.名字
     */
    public static final int SORT_NAME = 0;
    public static final int SORT_DATE = 1;
    public static final int SORT_SIZE = 2;
    public static final String[] SORTSHOW = {"按名字","按时间","按大小"};
    int currentSort = SORT_NAME;
    Comparator<AppInfo> comparator = null;
    //时间比较器(降序排序
    Comparator<AppInfo> dateComparator = new Comparator<AppInfo>() {
        @Override
        //左             右
        public int compare(AppInfo appInfo, AppInfo t1) {
            if(appInfo.lastUpdateTime > t1.lastUpdateTime){
                return -1;
            }
            else if(appInfo.lastUpdateTime == t1.lastUpdateTime){
                return 0;
            }
            else
                return 1;
        }
    };

    //大小比较器(降序排序
    Comparator<AppInfo> sizeComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo, AppInfo t1) {
            if(appInfo.byteSize > t1.byteSize){
                return -1;
            }
            else if(appInfo.byteSize == t1.byteSize){
                return 0;
            }
            else
                return 1;
        }
    };

    //名称比较器(正序,且不分字母大小
    Comparator<AppInfo> nameComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo appInfo, AppInfo t1) {
            //先全部变为小写再进行比较
            return appInfo.appName.toLowerCase().compareTo(t1.appName.toLowerCase());
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();     //所点击到的菜单item的id
        //判断所点击到的菜单item的id与哪个item的id一样
        if(id == R.id.refresh){
            updateDate();   //如果点击了刷新按钮则重新载入数据
            return true;  //并消化事件
        }
        if(id == R.id.sort_name){
           currentSort = SORT_NAME;
        }
        if(id == R.id.sort_date){
           currentSort = SORT_DATE;
        }
        if(id == R.id.sort_size){
           currentSort = SORT_SIZE;
        }
        update_sort(currentSort);
        return super.onOptionsItemSelected(item);
    }

    //再次写一个更新界面的方法,不能用原来那个,因为它会重新载入数据,则排序后的数据又会被打乱
    private void update_sort(int sort){
        if(sort == SORT_NAME){
            comparator = nameComparator;
        }
        if(sort == SORT_DATE){
            comparator = dateComparator;
        }
        if(sort == SORT_SIZE){
            comparator = sizeComparator;
        }
        Collections.sort(list,comparator);
        adapter.setList(list);
        adapter.notifyDataSetChanged();  //在原有界面进行更新
        tv_sort.setText("排序方式:"+SORTSHOW[currentSort]);
        tv_number.setText("app数量:"+list.size());
    }

    //旋转圈进度条刷新界面
    ProgressDialog progressDialog;
    public void progressDialogShow(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("刷新界面");
        progressDialog.setMessage("请稍后...");
        progressDialog.show();
    }

    //handler实现主线程与子线程的交互
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            update_sort(currentSort);
            progressDialog.dismiss();  //关闭进度条
        }
    };

    //运用线程实现更新
    public void updateDate(){
        new Thread(){
            @Override
            public void run() {
                list = Utils.getAppList(MainActivity.this);  //载入数据,进行更新
                KEY = null;
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);  //给主线程发送消息
            }
        }.start();  //子线程启动
        progressDialogShow();  //主线程显示转圈进度条,与上面的子线程一起进行
    }


    @Override
    /**
     * 点击行后启动程序
     * 参数: 1是哪个listview 2是当前listview的item的view的布局，就是可以用这个view，获取里面的控件的id后操作控件
     * 3是第几个item, 4 在listview中是第几行
     */
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppInfo app = (AppInfo) adapterView.getItemAtPosition(i);//获取点击行的item对象
        Utils.openPackage(this,app.packageName);
    }

    public static final int CODE_UNINSTALL = 0;//请求码
    @Override
    public void btn_uninstall(int i,String packageName) {
        Utils.uninstallApp(this,packageName,CODE_UNINSTALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CODE_UNINSTALL){
            updateDate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String KEY = null;
    @Override
    //点击搜索按钮
    public boolean onQueryTextSubmit(String s) {
        KEY = s;
        list = Utils.searchResult(list,s);
        update_sort(currentSort);
        return true; //消化事件
    }

    @Override
    public boolean onQueryTextChange(String s) {

        return true;  //消化事件
    }
}
