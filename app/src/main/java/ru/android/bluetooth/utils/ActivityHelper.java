package ru.android.bluetooth.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ActivityHelper {

    public static void startActivity(Activity from, Class to){
        Intent intent = new Intent(from, to);
        from.startActivity(intent);
    }
}
