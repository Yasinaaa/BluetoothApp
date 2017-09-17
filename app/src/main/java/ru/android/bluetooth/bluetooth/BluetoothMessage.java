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
        final String[] response = {""};
        this.mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if (msg.what != BluetoothCommands.CONNECTING_STATUS) {
                    byte[] r = (byte[]) msg.obj;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            String readMessage = null;
                            readMessage = IOUtils.toString(r, "utf-8");

                            response[0] += readMessage.trim().replaceAll("�", "");

                            //if(readMessage.length() > 2) readMessage.replace("OK","");
                            //if (readMessage.contains("3")) readMessage = "3";
                            Log.d("bm", readMessage);
                            mBluetoothMessageListener.onResponse(readMessage);

                            //mBluetoothMessageListener.onResponse(readMessage);
                            readMessage = null;
                            r = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //here call the second method

            }

        }, 1000000);
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
    /*public void writeMessageD(int[] data){
        mConnectedThread.writeData(data);
    }
    public void writeMessage(int[] data){
        mConnectedThread.write(data);
    }
    public void writeMessage(int data){
        mConnectedThread.write(data);
    }
    public void writeMessage(byte count, int data){
        mConnectedThread.write(count,data);
    }*/
    public void writeMessage(){
        mConnectedThread.writeeeeeeeeeeeeeeeeeee();
    }

    public void writeMessage(String message){
        mConnectedThread.writeData(BluetoothCommands.getStatusNum(message), message);
    }
    public void writeMessage(int[] listOn, int[] listOff){
        mConnectedThread.write(listOn, listOff);
    }
    public void wr(int count){
        mConnectedThread.writePPP(count);
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
