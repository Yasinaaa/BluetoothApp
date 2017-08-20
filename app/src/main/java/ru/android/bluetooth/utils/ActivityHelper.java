package ru.android.bluetooth.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import ru.android.bluetooth.R;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ActivityHelper {

    public static void startActivity(Activity from, Class to){
        Intent intent = new Intent(from, to);
        from.startActivity(intent);
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
