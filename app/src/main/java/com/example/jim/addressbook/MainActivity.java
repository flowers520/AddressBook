package com.example.jim.addressbook;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;




public class MainActivity extends AppCompatActivity {

    //声明控件
    ListView lv_menu;
    EditText et_search;

    //声明数据库操作对象
    SQLiteDatabase db;
    ArrayList<names> arr;
//    ArrayList<chars> arrChar;

    //声明一个定时器Handler加线程
    Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取洁面的listView
        lv_menu = (ListView)findViewById(R.id.lv_memu);
        et_search = (EditText)findViewById(R.id.et_search);
//        //让搜索栏失去焦点
//        et_search.clearFocus();

        //从DBHelper内产生一个操作对象
        db = new DBHelper(this).getWritableDatabase();

        //创建搜索定时器
        timer = new Timer();
        //启动计时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hand.sendEmptyMessage(1);
            }
        }, 0, 100);

        list();
        //lv_menu短按事件
        lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击电话数据
                final names nameClick = arr.get(position);
                //创建窗口意图
                Intent call = new Intent();
                //指定跳转事件
                call.setClass(getApplicationContext(), CallActivity.class);
                //利用bundle传值
                Bundle b = new Bundle();

//                Log.e("MainAction", nameClick.id+"");

                b.putString("callPhone", nameClick.id+"");
                call.putExtras(b);
                //开始意图并回调事件
                startActivityForResult(call, 0);


            }
        });



//        //长按事件
//        lv_menu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                //获取点击的电话数据
//                final names na = arr.get(position);
//                //创建提示选择框
//                AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
//                String[] items = {"删除"};
//                build.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //删除处理
//                        String sql = "delete from address where _id="+na.id;
//                        //实现操作
//                        db.execSQL(sql);
//                        //提示
//                        Toast.makeText(MainActivity.this, "删除成功", 0).show();
//                        //刷新数据
//                        list();
//                        }
//                });
//                build.show();
//                return false;
//            }
//        });



       }





    //刷新
    public void update(View view){
//        Toast.makeText(this, "你好吗", 0).show();
        et_search.setText("");
        list();
    }
    //跳转到添加窗口
    public void input(View view){
        //创建意图
        Intent it = new Intent();
        //制动跳转事件
        it.setClass(this, InputActivity.class);
        //开始启动意图，并实现回调事件
        startActivityForResult(it, 0);
    }
    //回调事件

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        list();
    }


    public void list(){

        Cursor cur = db.query("address",null,null,null,null,null,null);
        //声明一个集合放数据
        arr = new ArrayList<names>();
        //循环游标对象的数据进行一行一行的查询
        while (cur.moveToNext()){
            //获取游标内的每一个值
            int id = cur.getInt(0);
            String name = cur.getString(1);
            String num = cur.getString(2);
            String note = cur.getString(3);
            String head = cur.getString(4);
            //封装数据
            names n = new names(id, name, num, note, head);
            //数据存到即合理
            arr.add(n);
        }
        //关闭游标
        cur.close();

        //创建适配器
        namesAdapter nada = new namesAdapter();
        nada.arr = arr;
        nada.ctx = this;

        lv_menu.setAdapter(nada);
    }


    public void search(){
        arr.clear();
        String curr = et_search.getText()+"";

        //查找数据
        Cursor cur = db.query("address",null,"name like ? ",new String[]{"%"+curr+"%"},null,null,null);


        //循环游标一行一行的查找
        while (cur.moveToNext()){
            //获取游标里的内容
            int id = cur.getInt(0);
            String name = cur.getString(1);
            String num = cur.getString(2);
            String note = cur.getString(3);
            String head = cur.getString(4);
            //封装数据
            names n = new names(id, name, num, note, head);
            //数据存到即合理
            arr.add(n);

        }
        //关闭游标
        cur.close();
        //创建适配器
        namesAdapter nada = new namesAdapter();
        nada.arr = arr;
        nada.ctx = this;

        lv_menu.setAdapter(nada);
    }
    //定义全局的Handler定时器界面处理
    Handler hand = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1){
                search();
            }
            return false;
        }
    });

}
