package com.example.jim.addressbook;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class EditActivity extends AppCompatActivity {

    //声明控件
    EditText et_editname;
    EditText et_editnum;
    EditText et_editnote;
    ImageView iv_iamge;
    TextView tv_finish;
    //声明数据库操作
    SQLiteDatabase db;
    String ivEditPath="";
    //声明定时器
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //查找控件
        et_editname = (EditText)findViewById(R.id.et_editname);
        et_editnum = (EditText)findViewById(R.id.et_editnum);
        et_editnote = (EditText)findViewById(R.id.et_editnote);
        iv_iamge = (ImageView)findViewById(R.id.iv_editImage);
        tv_finish = (TextView)findViewById(R.id.tv_editFinish);
        //得到一个可写数据库
        db = new DBHelper(this).getWritableDatabase();

        String idCard = getIntent().getExtras().getString("editphone");
        et_editnum.setText(idCard+"");
        //查找数据
        Cursor cur = db.query("address",null,"_id=?",new String[]{idCard},null,null,null);

        //创建检索定时器
        timer = new Timer();
        //启动计时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hand.sendEmptyMessage(1);
            }
        }, 0, 100);

        //循环游标一行一行的查找
        while (cur.moveToNext()){
            //获取游标里的内容
            String name = cur.getString(1);
            String num = cur.getString(2);
            String note = cur.getString(3);
            String head = cur.getString(4);

            et_editname.setText(name);
            et_editnum.setText(num);
            et_editnote.setText(note);


        }
        //关闭游标
        cur.close();

    }

    //返回
    public void editOnCancel(View view){
        finish();
    }

    //完成
    public  void editOnFinish(View view){
        String idCard = getIntent().getExtras().getString("editphone");
        int id = Integer.parseInt(idCard);
        String name = et_editname.getText()+"";
        String num = et_editnum.getText()+"";
        String note = et_editnote.getText()+"";

        //sql语句
        String sql = "update address set name='"+name+"',num='"+num+"',note='"+note+"',head='"+ivEditPath+"' where _id="+id;
        db.execSQL(sql);
        finish();

    }

    //删除
    public void editOnDelete(View view){
        String idCard = getIntent().getExtras().getString("editphone");
        //删除处理
        String sql = "delete from address where _id="+idCard;
        //实现操作
        db.execSQL(sql);

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }

    //头像
    public void IVEdit(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    //回调方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Log.e("TVG->onresult", "ActivityResult resultCode error");
            return;
        }

        Bitmap bm = null;
        //外接程序访问ContentPriovider所提供的数据
        ContentResolver resolver = getContentResolver();
        Log.i("99", "99");

        //用于判断接受的Activity是不是想要的
        if (requestCode == 0) {

            try {
                //获取图片的uri
                Uri originalUri = data.getData();
                //显示得到的图片
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                iv_iamge.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    //定义全局的Handler定时器界面处理
    Handler hand = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1){
                //获得输入的数据
                String name = et_editname.getText()+"";
                String num = et_editnum.getText()+"";
                String note = et_editnote.getText()+"";
                if (name=="" && num=="" && note==""){
                    tv_finish.setEnabled(false);
                    tv_finish.setTextColor(0xffa1a3a6);
                }else {
                    tv_finish.setEnabled(true);
                    tv_finish.setTextColor(0xff0000ff);
                }
            }
            return false;
        }
    });
}
