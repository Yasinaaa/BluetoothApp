package ru.android.bluetooth.main;

/**
 * Created by yasina on 05.08.17.
 */

public interface MainModel {

    public interface AutoModeView{
        void addItemToDateRecyclerView();
    }
    void setDeviceTitle(String title);
    void setStatus(String status);
}
