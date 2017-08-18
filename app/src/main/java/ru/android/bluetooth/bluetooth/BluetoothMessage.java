package ru.android.bluetooth.bluetooth;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Created by yasina on 07.08.17.
 */

public class BluetoothMessage {

    private Handler mHandler;
    private static BluetoothMessage mBluetoothMessage;
    private BluetoothMessageListener mBluetoothMessageListener;
    private ConnectedThread mConnectedThread;

    private BluetoothMessage() {
        //this.mConnectedThread = connectedThread; ConnectedThread connectedThread
        this.mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BluetoothCommands.MESSAGE_READ){
                    String readMessage = null;
                    byte[] r = (byte[]) msg.obj;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            readMessage =  IOUtils.toString(r, "utf-8");
                            readMessage = readMessage.trim().replaceAll("�", "");
                            if(readMessage.length() > 2) readMessage.replace("OK","");
                            //Log.d("bm", readMessage);
                            mBluetoothMessageListener.onResponse(readMessage);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    public static BluetoothMessage createBluetoothMessage(){
        if (mBluetoothMessage == null){
            mBluetoothMessage = new BluetoothMessage();
        }
        return mBluetoothMessage;
    }

    public ConnectedThread getConnectedThread() {
        return mConnectedThread;
    }

    public void setConnectedThread(ConnectedThread mConnectedThread) {
        this.mConnectedThread = mConnectedThread;
    }
    /*public static BluetoothMessage createBluetoothMessage(ConnectedThread connectedThread){
        if (mBluetoothMessage == null){
            mBluetoothMessage = new BluetoothMessage(connectedThread);
        }
        return mBluetoothMessage;
    }
    */


    public void writeMessage(String message){
        mConnectedThread.write(message);
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
