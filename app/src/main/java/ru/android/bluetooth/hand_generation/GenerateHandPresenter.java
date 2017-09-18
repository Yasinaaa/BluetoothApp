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
    private Calendar mBeginDay, mEndDay;
    private int mOnTime, mOffTime;
    boolean mIsOnOrOff;

    public GenerateHandPresenter(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        mBeginDay = Calendar.getInstance();
        mEndDay = Calendar.getInstance();
        mEndDay.add(Calendar.YEAR, 1);
    }

    public void setOnClickListenerImageButton(final boolean isOnOrOff, ImageButton imageButton, final TextView tvTime){
        final Calendar calendar = Calendar.getInstance();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                    tvTime.setText(i + ":" + i1);
                                    if(isOnOrOff){
                                        mOnTime = i*60 + i1;
                                    }else {
                                        mOffTime = i*60 + i1;
                                    }
                                }
                            }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

    }

    public void setOnDateClickListenerImageButton(final boolean isOnOrOff, ImageButton imageButton, final TextView tvTime){
        final Calendar calendar = Calendar.getInstance();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePicker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        tvTime.setText(day + "." + month + "." + year);

                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.YEAR, year);
                        calendar1.set(Calendar.MONTH, month);
                        calendar1.set(Calendar.DAY_OF_MONTH, day);

                        if(isOnOrOff){
                            mBeginDay = calendar1;
                        }else {
                            mEndDay = calendar1;
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.show();
            }
        });

    }

    private String setMinutes(int h, int m){
        return String.valueOf(h*60 + m);
    }

    public void setOnFabClickListener(FloatingActionButton floatingActionButton){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity)
                        .setTitle("Автореле")
                        .setMessage("Данная функция не доступна в данной версии")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                dialog.show();*/
                int[] beginTime = new int[365];
                int[] endTime = new int[365];


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
