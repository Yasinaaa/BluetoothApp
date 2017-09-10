package ru.android.bluetooth.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import ru.android.bluetooth.R;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ActivityHelper {

    public static AlertDialog showProgressBar(Activity activity){
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setView(dialogView);
        AlertDialog dialog1 = dialog.create();
        dialog1.show();
        return dialog1;
    }

    public static void startBroadcastReceiver(Activity from){
        IntentFilter filter = new IntentFilter(AppDestroyBroadcastReceiver.TAG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        AppDestroyBroadcastReceiver broadcastReceiver = new AppDestroyBroadcastReceiver(from);
        LocalBroadcastManager loadBroadcastManager = LocalBroadcastManager.getInstance(from.getApplicationContext());
        loadBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    public static void startActivity(Activity from, Class to){
        Intent intent = new Intent(from, to);
        //sendToAppDestroyListener(from, true, to);
        from.startActivity(intent);
        //from.finish();

    }

    public static void sendToAppDestroyListener(Activity from, boolean onOrOff){
        Intent intent = new Intent(from, AppDestroyService.class);
        intent.putExtra(AppDestroyService.ACTIVITY, from.getLocalClassName());
        intent.putExtra(AppDestroyService.ACTIVITY_ON_OFF, onOrOff);
        from.startService(intent);
    }

    public static void sendToAppDestroyListener(Activity from, boolean onOrOff, Class to){
        Intent intent = new Intent(from, AppDestroyService.class);
        intent.putExtra(AppDestroyService.ACTIVITY, to.getCanonicalName());
        intent.putExtra(AppDestroyService.ACTIVITY_ON_OFF, onOrOff);
        from.startService(intent);
    }
    public static void setVisibleIcon(AppCompatActivity activity){

        activity.getSupportActionBar().setDisplayUseLogoEnabled(true);
        activity.getSupportActionBar().setLogo(activity.getResources().getDrawable(R.drawable.runline));
        activity.getSupportActionBar().setIcon(activity.getResources().getDrawable(R.drawable.runline));


        /*getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.runline));
        getSupportActionBar().setDisplayUseLogoEnabled(true);*/
        //  getActionBar().setIcon(getResources().getDrawable(R.drawable.runline));
        //getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.runline));
    }
}
