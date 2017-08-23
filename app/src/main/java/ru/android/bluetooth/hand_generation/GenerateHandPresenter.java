package ru.android.bluetooth.hand_generation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by yasina on 23.08.17.
 */

public class GenerateHandPresenter {
    private Activity mActivity;
    private Context mContext;

    public GenerateHandPresenter(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
    }

    public void setOnClickListenerImageButton(ImageButton imageButton, final TextView tvTime){
        final Calendar calendar = Calendar.getInstance();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                    tvTime.setText(i + ":" + i1);
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

    public void setCheckBoxLocation(final CheckBox checkButton, final TextInputLayout latitude, final TextInputLayout longitude){
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkButton.isChecked()){
                    latitude.setEnabled(true);
                    longitude.setEnabled(true);
                }else {
                    latitude.setEnabled(false);
                    longitude.setEnabled(false);
                }
            }
        });
    }

    public void setCheckBoxLocation(final CheckBox checkButton, final TextInputLayout latitude){
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkButton.isChecked()){
                    latitude.setEnabled(true);
                }else {
                    latitude.setEnabled(false);
                }
            }
        });
    }
}
