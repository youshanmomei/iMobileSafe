package com.huaiying.imobilesafe.db.dao;

/**
 * Created by admin on 2016/3/17.
 */
public class BlackInfo {
    public static final int TYPE_CALL = 0;
    public static final int TYPE_SMS = 1;
    public static final int TYPE_ALL = 2;

    public String number;
    public int type;
}
