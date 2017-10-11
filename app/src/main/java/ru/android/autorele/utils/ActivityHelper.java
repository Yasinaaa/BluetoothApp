package ru.android.autorele.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import ru.android.autorele.R;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ActivityHelper {

    public static final int REQUEST_READ_PERMISSION = 785;

    public static void requestReadPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        } else {

        }
    }

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
