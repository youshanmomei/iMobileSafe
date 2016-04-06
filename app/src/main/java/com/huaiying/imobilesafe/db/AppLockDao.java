package com.huaiying.imobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.huaiying.imobilesafe.db.dao.AppLockDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/4/6.
 */
public class AppLockDao {
    private AppLockDBHelper mHelper;
    private Context mContext;

    public AppLockDao(Context context) {
        this.mContext = context;
        mHelper = new AppLockDBHelper(context);
    }

    public boolean add(String packageName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppLockDB.AppLock.COLUMN_PACKAGE_NAME, packageName);
        long insert = db.insert(AppLockDB.AppLock.TABLE_NAME, null, values);

        mContext.getContentResolver().notifyChange(Uri.parse("content://org.huiying.db.applock"), null);
        db.close();

        return insert != -1;
    }

    public boolean delete(String packageName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        String whereClause = AppLockDB.AppLock.COLUMN_PACKAGE_NAME + "=?";
        String[] whereArgs = new String[]{packageName};
        int delete = db.delete(AppLockDB.AppLock.TABLE_NAME, whereClause, whereArgs);

        mContext.getContentResolver().notifyChange(Uri.parse("content://org.huaiying.db.applock"), null);

        db.close();
        return delete > 0;
    }

    public boolean findLock(String packageName) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select count(1) from " + AppLockDB.AppLock.TABLE_NAME + " where " + AppLockDB.AppLock.COLUMN_PACKAGE_NAME + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{packageName});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count > 0;
    }

    public List<String> findAll() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "select " + AppLockDB.AppLock.COLUMN_PACKAGE_NAME + " from " + AppLockDB.AppLock.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        List<String> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String packageName = cursor.getString(0);
                list.add(packageName);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

}
