package ru.android.bluetooth.common.date_time;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.main.MainActivity;

/**
 * Created by yasina on 26.09.17.
 */

public class DateTimeClickListener {

    private DateTimeView mDateTimeView;
    private DateParser mDateParser;

    public DateTimeClickListener(DateTimeView mDateTimeView, DateParser dateParser) {
        this.mDateTimeView = mDateTimeView;
        this.mDateParser = dateParser;
    }

    public void setDateClickListener(Button setDateBtn, final Activity activity){
        final Calendar calendar = Calendar.getInstance();
        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePickerDialog mDatePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth() + 1;
                        int day = datePicker.getDayOfMonth();

                        mDateTimeView.sendDateMessage(String.format("%s-%s-%s", new String[]{
                                mDateParser.setZeros(day), mDateParser.setZeros(month), String.valueOf(year) }),
                                year, month, day);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.show();
            }
        });
    }

    public void setTimeClickListener(Button setTimeBtn, final Activity activity){
        final Calendar calendar = Calendar.getInstance();
        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        mDateTimeView.sendTimeMessage(String.format("%s:%s", new String[]{
                                mDateParser.setZeros(i), mDateParser.setZeros(i1)}), i, i1);

                    }
                },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }
}