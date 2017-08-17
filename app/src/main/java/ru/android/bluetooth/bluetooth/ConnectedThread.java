package ru.android.bluetooth.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yasina on 07.08.17.
 */

public class ConnectedThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;
    private static ConnectedThread mConnectedThread;

    public static ConnectedThread createConnectedThread(BluetoothSocket socket,Handler handler){
        if (mConnectedThread == null){
            return new ConnectedThread(socket, handler);
        }else {
            return mConnectedThread;
        }
    }

    public static ConnectedThread createConnectedThread(){
        return mConnectedThread;
    }

    private ConnectedThread(BluetoothSocket socket,Handler handler) {
        this.mHandler = handler;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                bytes = mmInStream.available();
                if(bytes != 0) {
                    SystemClock.sleep(100);
                    bytes = mmInStream.available();
                    bytes = mmInStream.read(buffer, 0, bytes);
                    mHandler.obtainMessage(BluetoothCommands.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();

                break;
            }
        }
    }


    public void write(String input) {
        byte[] bytes = input.getBytes();
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    public void write(byte[] input) {
        try {
            mmOutStream.write(input);
        } catch (IOException e) { }
    }


  /*  public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }*/
}
