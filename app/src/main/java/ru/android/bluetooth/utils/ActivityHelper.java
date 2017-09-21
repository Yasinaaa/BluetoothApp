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

    private static View createView(Activity activity){
        LayoutInflater inflater = activity.getLayoutInflater();
        return inflater.inflate(R.layout.dialog_loading, null);
    }

    private static AlertDialog createDialog(Activity activity, View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setView(view);
        AlertDialog dialog1 = dialog.create();
        dialog1.show();
        return dialog1;
    }

    public static AlertDialog showProgressBar(Activity activity){
        View dialogView = createView(activity);
        return createDialog(activity, dialogView);
    }

    public static AlertDialog showProgressBar(Activity activity, String text){
        View dialogView = createView(activity);
        TextView textView = (TextView) dialogView.findViewById(R.id.tv_loading);
        textView.setText(text);
        return createDialog(activity, dialogView);
    }

    public static AlertDialog changeProgressBarText(AlertDialog alertDialog, String text){
        TextView textView = (TextView) alertDialog.findViewById(R.id.tv_loading);
        textView.setText(text);
        return alertDialog;
    }

    public static void hideProgressBar(AlertDialog alertDialog){
        alertDialog.dismiss();
        alertDialog.cancel();
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
