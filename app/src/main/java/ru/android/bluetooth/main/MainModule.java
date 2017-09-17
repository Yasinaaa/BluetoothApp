package ru.android.bluetooth.main;

/**
 * Created by yasina on 05.08.17.
 */

public interface MainModule {

    interface ManualModeView{
        void setDeviceTitle(String title);
        void setStatus(String status);
        void addItemToDateRecyclerView();
    }

    interface AutoModeView{
        void addItemToDateRecyclerView();
    }

    interface AutoModePresenter{

    }

    interface ManualModePresenter{

    }


}
