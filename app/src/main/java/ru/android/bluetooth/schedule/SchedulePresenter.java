package ru.android.bluetooth.schedule;

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

import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.main.MainActivity;

/**
 * Created by yasina on 18.08.17.
 */

public class SchedulePresenter {

    private Activity mActivity;
    private Context mContext;

    public SchedulePresenter(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
    }

    public void setOnClickListenerImageButton(ImageButton imageButton, final TextView tvTime){
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

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.show();
            }
        });

    }

    private String setMinutes(int h, int m){
        return String.valueOf(h*60 + m);
    }

    public void setNotAvailableDialog(FloatingActionButton floatingActionButton){
        /*floatingActionButton.setOnClickListener(new View.OnClickListener() {
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
          //      mBluetoothMessage.writeMessage(onList, offList);
         //   }
      //  });
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
