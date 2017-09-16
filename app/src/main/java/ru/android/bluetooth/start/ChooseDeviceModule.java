package ru.android.bluetooth.start;


/**
 * Created by yasina on 07.08.17.
 */

public interface ChooseDeviceModule {

    interface ChooseDeviceView{
        void error(String message);
        void goNext();
        void addDevice(String text);
    }


    interface ChooseDevicePresenter{

    }
}
