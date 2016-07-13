package com.example.jim.addressbook;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class InputActivity extends AppCompatActivity {

    //声明数据库操作对象
    SQLiteDatabase db;
    //声明控件
    EditText et_name;
    EditText et_num;
    EditText et_note;
    TextView tv_finish;
    ImageView iv_input;
    String ivpath = "";
    //声明一个定时器
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        //查找控件
        et_name = (EditText)findViewById(R.id.et_inputNames);
        et_num = (EditText)findViewById(R.id.et_inputNums);
        et_note = (EditText)findViewById(R.id.et_inputnote);
        iv_input = (ImageView)findViewById(R.id.iv_input);
        tv_finish = (TextView)findViewById(R.id.inputOnFinish);

        //创建数据库操作对象
        db = new DBHelper(this).getWritableDatabase();

        //创建检索定时器
        timer = new Timer();
        //启动计时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hand.sendEmptyMessage(1);
            }
        }, 0, 100);
    }

    //完成按钮
    public void inputOnFinish(View view){
        //获得输入的数据
        String name = et_name.getText()+"";

        String num = et_num.getText()+"";

        String note = et_note.getText()+"";

        if(ivpath == ""){
            ivpath="";
        }
        if (name=="" && num=="" && note==""){
            Toast.makeText(this, "请添加数据", 0).show();
        }else{
            //创建SQL语句
            String sql = "insert into address(name,num,note,head) values ('"+name+"','"+num+"','"+note+"','"+ivpath+"')";
            //实现添加功能
            db.execSQL(sql);
//            //提示添加成功
//            Toast.makeText(this, "添加成功", 0).show();
            finish();
        }

    }

    //取消按钮
    public void inputOnCancel(View view){
        finish();
        //提示返回
    }

    //头像
    public void IVInput(View view){
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
                iv_input.setImageBitmap(bm);

//                if (DocumentsContract.isDocumentUri(this, originalUri)){
//                    String WholeID = DocumentsContract.getDocumentId(originalUri);
//                    String[] id = WholeID.split(':')[1];
//                    String[] column = {MediaStore.Images.Media.DATA};
//                    String sel = MediaStore.Images.Media._ID+'=?';
//                    Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String []{id}, null);
//                    int columnIndex = cursor.getColumnIndex(column[0]);
//                    if (cursor.moveToFirst()){
//                        ivpath = cursor.getString(columnIndex);
//                        Log.i("--------0-0-0",ivpath);
//                    }
//                    cursor.close();

//                //获取图片路径
//                String[] proj = {MediaStore.Images.Media.DATA};
//                //选择图片的索引值
//                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
//                //将光标移至开头以免越界
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                Log.i("---------column_index", column_index + "");
//                cursor.moveToFirst();
//                ivpath = cursor.getString(column_index);
//                Log.i("---------ivpath", ivpath + "");

                //getImageAbsolutePath(this, originalUri);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    //定义全局的Handler定时器界面处理
    Handler hand = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1){
                //获得输入的数据
                String name = et_name.getText()+"";
                String num = et_num.getText()+"";
                String note = et_note.getText()+"";
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
