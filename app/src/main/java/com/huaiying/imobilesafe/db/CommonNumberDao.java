package com.huaiying.imobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by admin on 2016/4/3.
 */
public class CommonNumberDao {


    /**
     * search the count of the group
     * @param context
     * @return
     */
    public static int getGroupCount(Context context) {
        String path = new File(context.getFilesDir(), "commonnum.db").getAbsoluteFile().getAbsolutePath();

        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select count(1) from classlist";
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return  count;
    }

    /**
     * get the count of the groupPostion child
     * @param context
     * @param groupPosition
     * @return
     */
    public static int getChildCount(Context context, int groupPosition) {
        String path = new File(context.getFilesDir(), "commonnum.db").getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select count(1) from table" + (groupPosition+1);
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();

        return count;
    }

    /**
     * get the text corresponding to group
     * @param context
     * @param groupPosition
     * @return
     */
    public static String getGroupText(Context context, int groupPosition) {
        String path = new File(context.getFilesDir(), "commonnum.db").getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        String sql = "select name from classlist where idx=?";
        Cursor cursor = db.rawQuery(sql, new String[]{(groupPosition + 1) + ""});
        String result = "";
        if (cursor != null) {
            if (cursor.moveToNext()) {
                result = cursor.getString(0);
            }
            cursor.close();
        }
        db.close();

        return result;
    }

    /**
     * query the data corresponding to the child
     * @param context
     * @param groupPosition
     * @param childPosition
     * @return
     */
    public static String[] getChildText(Context context, int groupPosition, int childPosition) {
        String path = new File(context.getFilesDir(), "commonnum.db").getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select name,number from table" + (groupPosition + 1) + " where _id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{(childPosition + 1) + ""});

        String name = "";
        String number = "";
        if (cursor != null) {
            if (cursor.moveToNext()) {
                name = cursor.getString(0);
                number = cursor.getString(1);
            }
            cursor.close();
        }
        db.close();

        return new String[]{name, number};
    }
}
