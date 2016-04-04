package com.huaiying.imobilesafe.engine;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/4/4.
 */
public class SmsProvider {

    public static void smsBackup(final Context context, final OnSmsListener listener) {
        new AsyncTask<Void, Integer, Boolean>() {

            private final static int TYPE_SIZE = 0;
            private final static int TYPE_PROGRESS = 1;

            @Override
            protected Boolean doInBackground(Void... params) {

                //format
                /*

                <list>
                    <sms>
                        <address>13511111</address>
                        <data>13511111</data>
                        <type>1</type>
                        <body>saldfjal</body>
                    </sms>
                    <sms>
                    ...
                    </sms>
                </list>

                 */

                //format xml
                XmlSerializer serializer = Xml.newSerializer();
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "smsbackup.xml"));
                    serializer.setOutput(os, "utf-8");
                    serializer.startDocument("utf-8", true);
                    serializer.startTag(null, "list");

                    ContentResolver cr = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms/");
                    String[] projection = {"address", "date", "type", "body"};
                    Cursor cursor = cr.query(uri, projection, null, null, null);
                    if (cursor != null) {
                        int count = cursor.getCount();
                        publishProgress(TYPE_SIZE, count);

                        int progress = 0;
                        while (cursor.moveToNext()) {
                            serializer.startTag(null, "sms");

                            serializer.startTag(null, "address");
                            String address = cursor.getString(0);
                            if (TextUtils.isEmpty(address)) {
                                address = "";
                            }
                            serializer.text(address);
                            serializer.endTag(null, "address");

                            serializer.startTag(null, "date");
                            long data = cursor.getLong(1);
                            serializer.text(data + "");
                            serializer.endTag(null, "date");

                            serializer.startTag(null, "type");
                            int type = cursor.getInt(2);
                            serializer.text(type + "");
                            serializer.endTag(null, "type");

                            serializer.startTag(null, "body");
                            String body = cursor.getString(3);
                            if (TextUtils.isEmpty(body)) {
                                body = "";
                            }
                            serializer.text(body);
                            serializer.endTag(null, "body");

                            serializer.endTag(null, "sms");

                            Thread.sleep(300);

                            progress++;
                            publishProgress(TYPE_PROGRESS);

                        }
                        cursor.close();
                    }
                    serializer.endTag(null, "list");
                    serializer.endDocument();

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (listener != null) {
                                listener.onFailed();
                            }
                        }
                        os = null;
                    }
                }

            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (values[0] == TYPE_SIZE) {
                    if (listener != null) {
                        listener.onMax(values[1]);
                    }
                } else {
                    if (listener != null) {
                        listener.onProgress(values[1]);
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } else {
                    if (listener != null) {
                        listener.onFailed();
                    }
                }
            }
        }.execute();
    }

    public static void smsRestore(final Context context, final OnSmsListener listener) {
        new AsyncTask<Void, Integer, Boolean>() {

            private final static int TYPE_SIZE = 0;
            private final static int TYPE_PROGRESS = 1;

            @Override
            protected Boolean doInBackground(Void... params) {
                XmlPullParser parser = Xml.newPullParser();
                FileInputStream is = null;
                try {
                    is = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "smsbackup.xml"));

                    parser.setInput(is, "utf-8");

                    List<SmsInfo> list = null;
                    SmsInfo info = null;
                    int type = parser.getEventType();
                    do {
                        String name = parser.getName();
                        switch (type) {
                            case XmlPullParser.START_DOCUMENT:
                                list = new ArrayList<>();
                                break;
                            case XmlPullParser.START_TAG:
                                if ("sms".equals(name)) {
                                    info = new SmsInfo();
                                } else if ("address".equals(name)) {
                                    info.address = parser.nextText();
                                } else if ("data".equals(name)) {
                                    info.data = Long.valueOf(parser.nextText());
                                } else if ("type".equals(name)) {
                                    info.type = Integer.valueOf(parser.nextText());
                                } else if ("body".equals(name)) {
                                    info.body = parser.nextText();
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if ("sms".equals(name)) {
                                    list.add(info);
                                }
                                break;
                            default:
                                break;
                        }
                        type = parser.next();
                    } while (type != XmlPullParser.END_DOCUMENT);

                    publishProgress(TYPE_SIZE, list.size());

                    ContentResolver cr = context.getContentResolver();

                    Uri url = Uri.parse("content://sms/");
                    for (int i = 0; i < list.size(); i++) {
                        SmsInfo sms = list.get(i);

                        ContentValues values = new ContentValues();
                        values.put("address", sms.address);
                        values.put("data", sms.data);
                        values.put("type", sms.type);
                        values.put("body", sms.body);
                        cr.insert(url, values);

                        publishProgress(TYPE_PROGRESS, i);
                    }
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();

                            if (listener != null) {
                                listener.onFailed();
                            }
                        }
                        is = null;
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (values[0] == TYPE_SIZE) {
                    if (listener != null) {
                        listener.onMax(values[1]);
                    }
                } else {
                    if (listener != null) {
                        listener.onProgress(values[1]);
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } else {
                    if (listener != null) {
                        listener.onFailed();
                    }
                }
            }
        }.execute();
    }

    public interface OnSmsListener {
        void onMax(int max);

        void onProgress(int progress);

        void onSuccess();

        void onFailed();
    }

    private static class SmsInfo {
        String address;
        long data;
        int type;
        String body;
    }
}
