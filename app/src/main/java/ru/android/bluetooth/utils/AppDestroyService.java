package ru.android.bluetooth.utils;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ru.android.bluetooth.R;

/**
 * Created by yasina on 21.08.17.
 */

public class AppDestroyService extends IntentService {

    private final String TAG = "AppDestroyService";
    public static final String ACTIVITY = "activity_intent";
    public static final String ACTIVITY_ON_OFF = "activity_on_off_intent";


    public AppDestroyService() {
        super("AppDestroyService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent broadcastIntent = new Intent(this, AppDestroyBroadcastReceiver.class);
        broadcastIntent.putExtra(ACTIVITY, intent.getStringExtra(ACTIVITY));
        broadcastIntent.putExtra(ACTIVITY_ON_OFF, intent.getBooleanExtra(ACTIVITY_ON_OFF, false));
        sendBroadcast(broadcastIntent);
    }

}
