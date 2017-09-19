package ru.android.bluetooth.view;

import android.support.v4.widget.NestedScrollView;
import android.widget.TableLayout;

import java.util.Calendar;

/**
 * Created by yasina on 17.09.17.
 */

public interface CalendarModule {

    interface View{
        void onLoadingScheduleFinished();
    }

    interface Presenter{
        void setTable(TableLayout tableLayout);
        void getSchedule();
        void setLoadSchedule();
        void searchDay(String date, TableLayout tableLayout, NestedScrollView mNestedScrollView);
        void generateSchedule(Calendar startDate, Calendar endDate, double latitude, double longitude, int zone);
    }
}
