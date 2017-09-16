package ru.android.bluetooth.start;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by yasina on 07.08.17.
 */

public class ChooseDevicePresenter implements ChooseDeviceModule.ChooseDevicePresenter{

    private ChooseDeviceModule.ChooseDeviceView mChooseDeviceView;

    public ChooseDevicePresenter(ChooseDeviceModule.ChooseDeviceView mChooseDeviceView) {
        this.mChooseDeviceView = mChooseDeviceView;
    }


}
