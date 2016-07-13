package com.example.jim.addressbook;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context) {
        super(context, "book.db", null, 1);
    }

    //完成创表操作
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table address(_id integer primary key autoincrement,"+
                "name varchar(10) not null, num varchar(20), note varchar(50), head varchar(100))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
