package com.huaiying.imobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huaiying.imobilesafe.db.dao.BlackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/3/17.
 */
public class BlackDao {

    private final BlacklistDBHelper mHelper;

    public BlackDao(Context context) {
        mHelper = new BlacklistDBHelper(context);
    }


    /**
     * query partial data
     *
     * @param perPageSize :the number of data to query
     * @param index       :from what location to start query
     * @return
     */
    public List<BlackInfo> findPart(int perPageSize, int index) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //limit: the number of pages to be displayed per page
        //offset: when the  data from the database, from what position to take
        String sql = "select " + BlackListDB.BlackList.COLUMN_NUMBER + ","
                + BlackListDB.BlackList.COLUMN_TYPE + " from "
                + BlackListDB.BlackList.TABLE_NAME + " limit " + perPageSize
                + " offset " + index;

        Cursor cursor = db.rawQuery(sql, null);

        List<BlackInfo> list = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                int type = cursor.getInt(1);

                BlackInfo info = new BlackInfo();
                info.number = number;
                info.type = type;
                list.add(info);
            }
            cursor.close();
        }

        db.close();
        return list;
    }

    /**
     * delete number
     *
     * @param number
     * @return
     */
    public boolean delete(String number) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        String whereClause = BlackListDB.BlackList.COLUMN_NUMBER + "=?";
        String[] whereArgs = new String[]{number};
        int delete = db.delete(BlackListDB.BlackList.TABLE_NAME, whereClause, whereArgs);
        db.close();

        return delete != 0;
    }

    /**
     * update the type of number interceptor
     *
     * @param number
     * @param type
     * @return
     */
    public boolean update(String number, int type) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BlackListDB.BlackList.COLUMN_TYPE, type);
        String whereClause = BlackListDB.BlackList.COLUMN_NUMBER + "=?";
        String[] whereArgs = new String[]{number};
        int update = db.update(BlackListDB.BlackList.TABLE_NAME, values, whereClause, whereArgs);

        db.close();
        return update != 0;
    }

    /**
     * add number to database
     *
     * @param number
     * @param type
     * @return
     */
    public boolean add(String number, int type) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BlackListDB.BlackList.COLUMN_NUMBER, number);
        values.put(BlackListDB.BlackList.COLUMN_TYPE, type);

        long insert = db.insert(BlackListDB.BlackList.TABLE_NAME, null, values);

        db.close();

        return insert != -1;
    }

    /**
     * search black number type
     * @param number
     * @return
     */
    public int findType(String number) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select " + BlackListDB.BlackList.COLUMN_TYPE + " from "
                + BlackListDB.BlackList.TABLE_NAME + " where "
                + BlackListDB.BlackList.COLUMN_NUMBER + "?=";

        String[] selectionArgs = new String[]{number};

        Cursor cursor = db.rawQuery(sql, selectionArgs);
        int type = -1;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                type = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return type;
    }
}
