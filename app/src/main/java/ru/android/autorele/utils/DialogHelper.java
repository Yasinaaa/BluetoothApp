package ru.android.autorele.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.android.autorele.R;

/**
 * Created by yasina on 22.09.17.
 */

public class DialogHelper {

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

    public static AlertDialog showSuccessMessage(Activity activity, String title, String text){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(text)
                .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog1 = dialog.create();
        dialog.show();
        return dialog1;
    }

    public static AlertDialog showErrorMessage(Activity activity, String text){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.mistake))
                .setMessage(text)
                .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog1 = dialog.create();
        dialog.show();
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
}
