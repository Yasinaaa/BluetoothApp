package ru.android.autorele.common.date_time;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by yasina on 26.09.17.
 */

public class DateTimeClickListener {


    public static void setDateClickListener(final Activity activity, final DateTimeView.TimeAndDate mDateTimeView,
                                            final DateParser mDateParser) {

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog mDatePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                mDateTimeView.sendDateMessage(String.format("%s-%s-%s", new String[]{
                                mDateParser.setZeros(day), mDateParser.setZeros(month + 1), String.valueOf(year)}),
                        year, month, day);

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDatePicker.show();
    }

    public static void setTimeClickListener(final Activity activity, final DateTimeView.TimeAndDate mDateTimeView,
                                            final DateParser mDateParser) {

        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                mDateTimeView.sendTimeMessage(String.format("%s:%s", new String[]{
                        mDateParser.setZeros(i), mDateParser.setZeros(i1)}), i, i1);

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public static void setTimeClickListener(final String type, final Activity activity,
                                            final DateTimeView.Time mDateTimeView,
                                            final DateParser mDateParser) {

        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                mDateTimeView.sendTimeMessage(type, String.format("%s:%s", new String[]{
                        mDateParser.setZeros(i), mDateParser.setZeros(i1)}), setMinutes(i, i1));

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private static String setMinutes(int h, int m){
        return String.valueOf(h*60 + m);
    }
}
