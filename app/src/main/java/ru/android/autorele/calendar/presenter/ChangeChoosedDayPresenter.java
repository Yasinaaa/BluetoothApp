package ru.android.autorele.calendar.presenter;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.android.autorele.common.date_time.DateParser;

/**
 * Created by yasina on 23.08.17.
 */

public class ChangeChoosedDayPresenter {

    private Activity mActivity;
    private Context mContext;
    private DateParser mDateParser;

    public ChangeChoosedDayPresenter(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        mDateParser = new DateParser();
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
                        tvTime.setText(mDateParser.setZeros(i) + ":" + mDateParser.setZeros(i1));
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


            }
        });
    }

    public void getSchedule(){

    }

}
