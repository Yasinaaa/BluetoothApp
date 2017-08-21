package ru.android.bluetooth.utils;

import android.content.BroadcastReceiver;
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

public class AppDestroyBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "AppDestroyBR";
    private Map<String, Boolean> mActivities;
    private Context mContext;
    private Activity mActivity;

    public AppDestroyBroadcastReceiver(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        getAllActivities();
    }

    private void getAllActivities(){
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getString(R.string.app_packages),
                    PackageManager.GET_ACTIVITIES);
            ActivityInfo[] list = info.activities;
            createMapForActivities(list);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createMapForActivities(ActivityInfo[] list){
        mActivities = new HashMap<String, Boolean>();
        for (ActivityInfo activityInfo: list){
            mActivities.put(activityInfo.name, false);
        }
    }


    public void onActivityModeListener(String currentActivity, boolean onOf) {

        mActivities.put(currentActivity, onOf);
        Log.d(TAG, "curent=" + currentActivity + " onOf=" + onOf);

        if (!mActivities.values().contains(true)){
            BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
            mBTAdapter.disable();
            mBTAdapter.cancelDiscovery();

            LocalBroadcastManager loadBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            loadBroadcastManager.unregisterReceiver(this);
            Intent myService = new Intent(mActivity, AppDestroyService.class);
            mActivity.stopService(myService);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        onActivityModeListener(intent.getStringExtra(AppDestroyService.ACTIVITY),
                intent.getBooleanExtra(AppDestroyService.ACTIVITY_ON_OFF, false));
    }
}
