package ru.android.bluetooth.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.android.bluetooth.R;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ActivityHelper {

    public static void startActivity(Activity from, Class to){
        Intent intent = new Intent(from, to);
        from.startActivity(intent);
    }

    public static void startActivityAndFinishThis(Activity from, Class to){
        Intent intent = new Intent(from, to);
        from.startActivity(intent);
        from.finish();
    }

    public static void setVisibleLogoIcon(AppCompatActivity activity){
        activity.getSupportActionBar().setDisplayUseLogoEnabled(true);
        activity.getSupportActionBar().setLogo(activity.getResources().getDrawable(R.drawable.runline));
        activity.getSupportActionBar().setIcon(activity.getResources().getDrawable(R.drawable.runline));
    }
}
