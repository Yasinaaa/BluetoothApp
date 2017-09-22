package ru.android.bluetooth.bluetooth;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Created by yasina on 07.08.17.
 */

public class BluetoothMessage {

    private final String TAG = "BluetoothMessage";
    private Handler mHandler;
    private static BluetoothMessage mBluetoothMessage;
    private BluetoothMessageListener mBluetoothMessageListener;
    private ConnectedThread mConnectedThread;

    private BluetoothMessage() {

        this.mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if (msg.what != BluetoothCommands.CONNECTING_STATUS) {
                    byte[] r = (byte[]) msg.obj;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            String readMessage = null;
                            readMessage = IOUtils.toString(r, "utf-8");
                            readMessage = readMessage.trim().replaceAll("�", "");
                            mBluetoothMessageListener.onResponse(readMessage);
                            readMessage = null;
                        } catch (IOException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
            }
        };
        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 1000000);
    }

    public static BluetoothMessage createBluetoothMessage(){
        if (mBluetoothMessage == null){
            mBluetoothMessage = new BluetoothMessage();
        }
        return mBluetoothMessage;
    }

    public void setConnectedThread(ConnectedThread mConnectedThread) {
        this.mConnectedThread = mConnectedThread;
    }

    public void writeMessage(Activity activity, String message){
        mConnectedThread.writeData(activity, message);
    }
    public void writeMessage(Activity activity, int[] listOn, int[] listOff){
        mConnectedThread.write(activity, listOn, listOff);
    }

    public Handler getHandler() {
        return mHandler;
    }

    public interface BluetoothMessageListener{
        void onResponse(String answer);
    }

    public void setBluetoothMessageListener(BluetoothMessageListener mBluetoothMessageListener) {
        this.mBluetoothMessageListener = mBluetoothMessageListener;
    }
}
