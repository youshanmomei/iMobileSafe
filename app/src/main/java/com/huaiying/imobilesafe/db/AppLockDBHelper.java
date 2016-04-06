package com.huaiying.imobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huaiying.imobilesafe.db.dao.AppLockDB;

/**
 * Created by admin on 2016/4/6.
 */
public class AppLockDBHelper extends SQLiteOpenHelper{
    public AppLockDBHelper(Context context) {
        super(context, AppLockDB.DB, null, AppLockDB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table for database
        db.execSQL(AppLockDB.AppLock.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
