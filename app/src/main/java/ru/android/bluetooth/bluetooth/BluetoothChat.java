package ru.android.bluetooth.bluetooth;

/**
 * Created by yasina on 07.08.17.
 */

public class BluetoothChat {

    private String mAddress, mName;
    private ConnectedThread mConnectedThread;

    public BluetoothChat(String mAddress, String mName, ConnectedThread mConnectedThread) {
        this.mAddress = mAddress;
        this.mName = mName;
        this.mConnectedThread = mConnectedThread;
    }

    private void write(String text){
        mConnectedThread.write(text);
    }

    private void read(){

    }
}
