package com.huaiying.imobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by admin on 2016/3/28.
 */
public class AntiVirusDao {

    /**
     * to determine whether the virus is
     * @param context
     * @param md5
     * @return
     */
    public static boolean isVirus(Context context, String md5) {
        String path = new File(context.getFilesDir(), "antivirus.db").getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        String sql = "select count(1) from datable where md5=?";
        Cursor cursor = db.rawQuery(sql, new String[]{md5});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();

        return count>0;
    }


}
