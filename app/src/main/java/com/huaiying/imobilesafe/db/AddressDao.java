package com.huaiying.imobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by admin on 2016/4/1.
 */
public class AddressDao {
    public static String findAddress(Context context, String number) {
        String path = new File(context.getFilesDir(), "address.db").getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        boolean isPhone = number.matches("^1[34578]\\d{9}$");
        String address = null;
        if (isPhone) {
            //search telephone number
            String sql = "select cardtype from info where mobileprefix=?";

            String prefix = number.substring(0, 7);
            Cursor cursor = db.rawQuery(sql, new String[]{prefix});
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    address = cursor.getString(0);
                }
                cursor.close();
            }

        } else {
            int length = number.length();

            switch (length) {
                case 3:
                    address = "紧急电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "服务号码";
                    break;
                case 6:
                    address = "";
                    break;
                case 7:
                case 8:
                    address = "本地座机";
                    break;
                case 10:
                case 11:
                case 12:
                    //search
                    String prefix = number.substring(0, 3);

                    String sql = "select city from info where area=?";
                    Cursor cursor = db.rawQuery(sql, new String[]{prefix});
                    if (cursor != null) {
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                        cursor.close();
                    }

                    if (TextUtils.isEmpty(address)) {
                        prefix = number.substring(0, 4);
                        cursor = db.rawQuery(sql, new String[]{prefix});
                        if (cursor != null) {
                            if (cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }
                            cursor.close();
                        }
                    }

                    if (TextUtils.isEmpty(address)) {
                        address = "未知";
                    }

                default:
                    address = "未知";
                    break;

            }
        }

        db.close();
        return  address;

    }
}
