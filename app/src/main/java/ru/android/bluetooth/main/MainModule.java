package ru.android.bluetooth.main;

/**
 * Created by yasina on 05.08.17.
 */

public interface MainModule {

    interface View{
        void dataCreated(int[] onList, int[] offList);
        void setDeviceTitle(String title);
        void setStatus(String status);
        void requestReadPermission();
        void setScheduleTitle(String title);
    }

    interface Presenter{

    }


}
