package ru.android.autorele.common.date_time;

/**
 * Created by yasina on 26.09.17.
 */

public interface DateTimeView {

    interface TimeAndDate{
        void sendDateMessage(String thisTextNeedToSetTextView, int year, int month, int day);
        void sendTimeMessage(String thisTextNeedToSetTextView, int hour, int min);
    }

    interface Time{
        void sendTimeMessage(String type, String timeView, String minView);
    }

}
