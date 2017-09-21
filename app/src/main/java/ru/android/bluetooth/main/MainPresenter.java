package ru.android.bluetooth.main;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import ru.android.bluetooth.bluetooth.BluetoothMessage;

/**
 * Created by yasina on 22.08.17.
 */

public class MainPresenter {

    private final String TAG = "MainPresenter";
    private Context mContext;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private BluetoothMessage mBluetoothMessage;

    public MainPresenter(Context mContext, BluetoothMessage bluetoothMessage) {
        this.mContext = mContext;
        this.mBluetoothMessage = bluetoothMessage;
    }


}
