package ru.android.bluetooth.start;

import ru.android.bluetooth.common.CommonView;

/**
 * Created by yasina on 07.08.17.
 */

public interface ChooseDeviceView extends CommonView{

    void goNext();
    void addDevice(String text);
}
