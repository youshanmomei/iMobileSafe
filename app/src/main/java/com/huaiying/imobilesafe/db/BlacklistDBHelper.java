package com.huaiying.imobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huaiying.imobilesafe.util.Logger;

/**
 * Created by admin on 2016/3/17.
 */
public class BlacklistDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "BlacklistDBHelper";

    public BlacklistDBHelper(Context context) {
        super(context, BlackListDB.DB_NAME, null, BlackListDB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //init database
        //create table
        String sql = BlackListDB.BlackList.SQL_CREATE_TAVLE;
        Logger.d(TAG, "" + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
