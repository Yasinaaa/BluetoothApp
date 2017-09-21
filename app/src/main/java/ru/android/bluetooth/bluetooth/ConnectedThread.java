package ru.android.bluetooth.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.zip.CRC32;

import ru.android.bluetooth.R;
import ru.android.bluetooth.schedule.ScheduleWriter;


/**
 * Created by yasina on 07.08.17.
 */

public class ConnectedThread extends Thread {

    private final String TAG = "ConnectedThread";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;
    private static ConnectedThread mConnectedThread;

    public static ConnectedThread createConnectedThread(BluetoothSocket socket,Handler handler){
        /*if (mConnectedThread == null){
            return new ConnectedThread(socket, handler);
        }else {
            return mConnectedThread;
        }*/
        return new ConnectedThread(socket, handler);
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

        int bytes;
        byte[] buffer;
        while (true) {
            try {
                bytes = mmInStream.available();

                buffer = new byte[bytes];

                if(bytes != 0) {
                    SystemClock.sleep(1000);
                    try {
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mHandler.obtainMessage(BluetoothCommands.MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    }catch (java.lang.ArrayIndexOutOfBoundsException e){
                        Log.e(TAG, "bytes=" + bytes);
                    }

                }else {

                }
            } catch (IOException e) {
                e.printStackTrace();

                break;
            }
        }

    }

    public void write(int[] listOn, int[] listOff){
        ScheduleWriter s = new ScheduleWriter();
        s.write(mmOutStream, listOn, listOff);
    }

    public String writeData(Activity activity, String data) {
        try {
            mmOutStream.write(data.getBytes());
        } catch (IOException e) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.connection_failed))
                    .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            dialogBuilder.show();
            Log.d(TAG, e.getMessage());
            return e.getMessage();
        }
        return null;
    }
}
