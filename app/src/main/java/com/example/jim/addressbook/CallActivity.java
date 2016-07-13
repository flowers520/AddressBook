package com.example.jim.addressbook;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;

public class CallActivity extends AppCompatActivity {

    //声明控件
    TextView tv_callname;
    TextView tv_callNum;
    TextView et_callnote;
    ImageView iv_touxiang;

    //声明数据库操作对象
    SQLiteDatabase db;
    //定义两个变量
    String nameCall;
    String numCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        //查找控件
        tv_callname = (TextView)findViewById(R.id.tv_callName);
        tv_callNum = (TextView)findViewById(R.id.tv_callnum);
        et_callnote = (TextView)findViewById(R.id.et_callnote);
        iv_touxiang = (ImageView)findViewById(R.id.iv_touxiang);

        //从DBHelper内产生一个操作对象
        db = new DBHelper(this).getWritableDatabase();


        list();

    }

    //短信按钮
    public void imageBuDx(View view){
        //创建电话号码
        String tel = numCall;
        //发短信
        Intent intent = new Intent("android.intent.action.SENDTO");
        intent.setData(Uri.parse("smsto://"+tel));
        startActivity(intent);
    }

    //电话按钮
    public void imageBuDh(View view){
        //创建电话号码
        String tel = numCall;
//        //打电话
//        Intent intent = new Intent("android.intent.action.CALL");
//        intent.setData(Uri.parse("tel:"+tel));
//        startActivity(intent);
        //播号盘
        Uri uri = Uri.parse("tel:"+tel);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        this.startActivity(intent);

    }

    //返回按钮
    public void callOnCancel(View view){
        finish();
    }

    //编辑按钮
    public  void callOnEdit(View view){
        //创建意图
        Intent intent = new Intent();
        intent.setClass(this, EditActivity.class);
        Bundle b = new Bundle();
        String callphone = getIntent().getExtras().getString("callPhone");
        b.putString("editphone", callphone);
        intent.putExtras(b);
        startActivityForResult(intent, 0);
    }
    //回调事件


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        list();
    }

    public void list(){
        String MainString = getIntent().getExtras().getString("callPhone");
        //查找数据
        Cursor cur = db.query("address",null,"_id=?",new String[]{MainString},null,null,null);


        //循环游标一行一行的查找
        while (cur.moveToNext()){
            //获取游标里的内容
            String name = cur.getString(1);
            String num = cur.getString(2);
            String note = cur.getString(3);
            String head = cur.getString(4);
            //给变量赋值
            nameCall = name;
            numCall = num;

            tv_callname.setText(name);
            tv_callNum.setText(num);
            et_callnote.setText(note);

            Log.i("Call", head);
            if (head.equals("/storage/emulated/0/")){
                iv_touxiang.setImageResource(R.drawable.tt);
            }else{
                Bitmap bm = BitmapFactory.decodeFile(head);
                iv_touxiang.setImageBitmap(bm);
            }


        }
        //关闭游标
        cur.close();
    }

}
