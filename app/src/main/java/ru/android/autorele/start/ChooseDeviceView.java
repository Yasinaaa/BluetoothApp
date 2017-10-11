package ru.android.autorele.start;


/**
 * Created by yasina on 07.08.17.
 */

public interface ChooseDeviceView {

    void error(String message);
    void goNext();
    void addDevice(String text);

}
