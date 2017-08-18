package ru.android.bluetooth.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.zip.CRC32;

import ru.android.bluetooth.schedule.helper.S;

import static java.lang.Long.BYTES;


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

    public void write(byte count, int data) {

        ByteBuffer bufferNumber = ByteBuffer.allocate(Byte.SIZE);
        bufferNumber = longToBytesBuffer(data);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.SIZE);
        CRC32 crc = new CRC32();
        crc.update(data);
        byteBuffer.putLong(crc.getValue());

        byte[] bytes = combineBytes(count, bufferNumber.array(), byteBuffer.array());

        try {
            mmOutStream.write(bytes);

            mmOutStream.write(BluetoothCommands.DEBUG.getBytes());
        } catch (IOException e) {
            Log.d("T", e.getMessage());
        }

    }

    int checksum(int[] array, int size) {
        int c = 0;
        for(int i = 0; i < size; i++) {
            c += array[i];
            c = c << 3 | c >> (32 - 3); // rotate a little
            c ^= 0xFFFFFFFF; // invert just for fun
        }
        return c;
    }

    private byte crc(int num){

        byte answer = (byte)num;
        byte one = 1;
        byte t = (byte) (answer + one);

        return t;
    }

    public void write(int count) {

        ByteBuffer byteBufferData = ByteBuffer.allocate(4);
        IntBuffer intBuffer = byteBufferData.asIntBuffer();
        intBuffer.put(count);

        ByteBuffer crcBuffer = ByteBuffer.allocate(32);
        /*CRC32 crc = new CRC32();
        crc.update(count);
        crcBuffer.putLong(crc.getValue());*/

        //crcBuffer.putInt(checksum(count));
        byte[] one = byteBufferData.array();

        //byte crc = crc(count);

       // byte[] bytes = combineBytes(byteBufferData.array(), crc);
        //String s = makeCrc(count);
        numToStr(count);
        numToStrReverse(count);
        try {
            mmOutStream.write(("Set Data\r\n" + 40).getBytes());
            //mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }
    }


    private String strToPackCRC(String s){
        int crc;
        String so;

        if(s.length() != 0){
            so = s.length() + s;
            crc = 0;
           // for()
        }
        return null;
    }



    private String numToStrReverse(int n){
        String s=n%256 + "";
        n=n/256;
        s = s + n%256;
        Log.d("T","Reverse=" + s);
        return s;
    }

    private String numToStr(int n){
        String s=n%256 + "";
        n=n/256;
        s = n%256+s;
        Log.d("T", s);
        return s;
    }

    public void write(int[] data) {

        ByteBuffer byteBufferData = ByteBuffer.allocate(data.length * Byte.SIZE);
        IntBuffer intBuffer = byteBufferData.asIntBuffer();
        intBuffer.put(data);
        byte[] dataBytes = byteBufferData.array();

        /*ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.SIZE);
        CRC32 crc = new CRC32();
        crc.update(dataBytes);
        byteBuffer.putLong(crc.getValue());*/

       // byte[] bytes = combineBytes(byteBufferData.array(), byteBuffer.array());

        try {
            mmOutStream.write(dataBytes);

            //mmOutStream.write(BluetoothCommands.DEBUG.getBytes());
        } catch (IOException e) {
            Log.d("T", e.getMessage());
        }

    }

    public byte[] combineBytes(byte[] one, byte[] two) {
        byte[] combined = new byte[one.length + two.length];
        System.arraycopy(one,0,combined,0,one.length);
        System.arraycopy(two,0,combined,one.length,two.length);
        return combined;
    }

   /* private byte[] crc(byte[] numAtBytes){
        byte[] newByteArray = new byte[8];
        System.arraycopy(numAtBytes,0,newByteArray, newByteArray.length - 8, numAtBytes.length);


        //numAtBytes[newByteArray.length - 1] + 1;
    }*/



    public byte[] combineBytes(byte count, byte[] data, byte[] crc) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            out.write(count);
            out.write(data);
            out.write(crc);
            out.write("\n".getBytes());
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();

            Log.e("TAG", e.getMessage());
        }

       return null;
    }

    public byte[] combineBytes(byte[] one, byte[] two, byte[] three) {

        byte[] combined = new byte[one.length + two.length + three.length];
        System.arraycopy(one,0,combined,0,one.length);
        System.arraycopy(two,0,combined,one.length,two.length);
        System.arraycopy(three, 0, combined, two.length, three.length);
        return combined;
    }

    public byte[] combineBytes(byte one, byte[] two) {
        byte[] combined = new byte[1 + two.length];
        System.arraycopy(one,0,combined,0, 1);
        System.arraycopy(two,0,combined,1,two.length);
        return combined;
    }

    public byte[] combineBytes(byte[] two, byte one) {
        byte[] combined = new byte[1 + two.length];
        System.arraycopy(two,0,combined,0, two.length);
        combined[two.length] = one;
        return combined;
    }


    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Byte.SIZE);
        buffer.putLong(x);
        return buffer.array();
    }

    public ByteBuffer longToBytesBuffer(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Byte.SIZE);
        buffer.putLong(x);
        return buffer;
    }

    public Long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Byte.SIZE);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

  /*  public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }*/
}
