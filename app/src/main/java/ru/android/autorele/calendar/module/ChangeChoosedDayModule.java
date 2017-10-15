package ru.android.autorele.calendar.module;

/**
 * Created by yasina on 14.10.17.
 */

public interface ChangeChoosedDayModule {

    interface View{
        String getSunriseTimeValue();
        String getSunsetTimeValue();
        void setSunriseTime(String timeView, String minView);
        void setSunsetTime(String timeView, String minView);
    }

    interface Presenter{
        void onFabClickListener();
    }

}
