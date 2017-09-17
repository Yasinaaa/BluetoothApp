package ru.android.bluetooth.view;

import android.widget.TableLayout;

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
    }
}
