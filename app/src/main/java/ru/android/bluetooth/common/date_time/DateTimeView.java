package ru.android.bluetooth.common.date_time;

/**
 * Created by yasina on 26.09.17.
 */

public interface DateTimeView {
    void sendDateMessage(String thisTextNeedToSetTextView, int year, int month, int day);
    void sendTimeMessage(String thisTextNeedToSetTextView, int hour, int min);
}
