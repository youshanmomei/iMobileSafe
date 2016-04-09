package com.huaiying.imobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.Logger;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

public class GPSService extends Service {

    private static String TAG = "GPSService";
    private LocationManager mLm;

    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(TAG, "gps服务开启");

        mLm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = 5*1000;
        float minDistance = 10;

//        mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.d(TAG, "gps服务关闭");
//        mLm.removeUpdates(listener);
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            float accuracy = location.getAccuracy();
            double altitude = location.getAltitude();
            float bearing = location.getBearing();
            float speed = location.getSpeed();

            Logger.d(TAG, "latitude:" + latitude);
            Logger.d(TAG, "longitude:" + longitude);

            loadLocation(longitude, latitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void loadLocation(double longitude, double latitude) {
        String url = "http://lbs.juhe.cn/api/getaddressbylngb";

//        ...
    }

    private void sendSms(String text) {
        SmsManager sm = SmsManager.getDefault();
        String address = SharePreferenceUtils.getString(this, Constants.SJFD_NUMBER);
        Logger.d(TAG, "address:" + address);
        sm.sendTextMessage(address, null, text, null, null);
    }


}
