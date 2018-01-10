package com.rahbod.androidproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, "MYDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS sounds(id integer primary key autoincrement, voice blob, " +
                "title VARCHAR(100), username VARCHAR(50));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Integer deleteSound(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("sounds",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }
}