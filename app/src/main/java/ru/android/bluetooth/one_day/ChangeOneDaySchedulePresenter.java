package ru.android.bluetooth.one_day;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.main.MainActivity;

/**
 * Created by yasina on 23.08.17.
 */

public class ChangeOneDaySchedulePresenter {

    private Activity mActivity;
    private Context mContext;

    public ChangeOneDaySchedulePresenter(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
    }

    public void setOnClickListenerImageButton(ImageButton imageButton, final TextView tvTime,
                                              final TextView tvMinutes, final String status){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        tvTime.setText(i + ":" + i1);
                        tvMinutes.setText(status + " " + setMinutes(i, i1));
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }

    private String setMinutes(int h, int m){
        return String.valueOf(h*60 + m);
    }

    public void setNotAvailableDialog(FloatingActionButton floatingActionButton){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity)
                        .setTitle("Автореле")
                        .setMessage("Данная функция не доступна в данной версии")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                dialog.show();
            }
        });
    }

}
