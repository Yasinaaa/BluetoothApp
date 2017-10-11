package ru.android.autorele.calendar;

import java.util.ArrayList;
import java.util.Calendar;

import ru.android.autorele.calendar.view.CalendarFragment;

/**
 * Created by yasina on 17.09.17.
 */

public interface CalendarModule {

    interface View{
        void onLoadingScheduleFinished();
        void dataCreated(int[] onList, int[] offList);
    }

    interface Presenter{
        ArrayList<Day> setTable();
        void getSchedule();
        void setLoadSchedule();
        //void searchDay(String date, CalendarFragment calendarFragment, ViewPager viewPager);
        void generateSchedule(Calendar startDate, Calendar endDate, double latitude, double longitude, int zone);
        void generateSchedule();
        void saveChanges(int dayOfYear, int dayOfMonth, int on, int off, CalendarFragment calendarFragment);
    }

    interface OnItemClicked {
        void onItemClick(int month, int day, String text, String on, String off);
    }
}
