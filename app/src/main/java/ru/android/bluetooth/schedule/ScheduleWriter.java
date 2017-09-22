package ru.android.bluetooth.schedule;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yasina on 20.08.17.
 */

public class ScheduleWriter {

    private static final String TAG = "ScheduleWriter";

    public static void write(Activity activity, OutputStream mmOutStream, int[] lisOn, int[] listOff) {
        try {
            byte[] setDataArray = new byte[]{2, 5, (byte)184, (byte)191};

            int[] combined = combineBytes(lisOn, listOff);
            List<int[]> numbers = divideTo128ByteElements(combined);
            List<byte[]> numsToBytesArray = new ArrayList<byte[]>();
            for (int i = 0; i< numbers.size(); i++){
                numsToBytesArray.add(getValues(numbers.get(i)));
            }

            mmOutStream.write("Set Data\r".getBytes());
            mmOutStream.write(setDataArray);

            for (int i=0; i<numsToBytesArray.size();i++) {
                SystemClock.sleep(1000);
                mmOutStream.write(numsToBytesArray.get(i));
            }
            SystemClock.sleep(1000);
            mmOutStream.write("ddd\r\n".getBytes());

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    public static int[] combineBytes(int[] one, int[] two) {
        int[] combined = new int[one.length + two.length];
        System.arraycopy(one,0,combined,0,one.length);
        System.arraycopy(two,0,combined,one.length,two.length);
        return combined;
    }

    private static ArrayList<int[]> divideTo128ByteElements(int[] array){
        ArrayList<int[]> elements = new ArrayList<int[]>();
        int num = 64;
        int count = array.length / num; // 11
        int mod = array.length - num * count;
        int[] temp;
        for (int i=0; i<= count; i++){
            temp = new int[num];
            if(i == 0){
                System.arraycopy(array, 0, temp, 0, num);
            }else if(i==count){
                temp = new int[mod];
                temp = Arrays.copyOfRange(array, num*i, num*i + mod);
            }else {
                temp = Arrays.copyOfRange(array, num*i, num*i + num);
            }
            elements.add(temp);
        }

        return elements;
    }

    private static byte[] getValues(int[] a){

        byte[] data = new byte[a.length*2  + 2];
        data[0] = (byte)(data.length - 2);
        int crc = data.length - 2;

        byte[] value;
        int j = 0;
        for (int i=0; i<a.length; i++){

            value = BigInteger.valueOf(a[i]).toByteArray();
            if (value.length == 1){

                data[j + 1] = value[0];
                data[j + 2] = 0;
            }else {
                data[j + 1] = value[1];
                data[j + 2] = value[0];
            }

            crc += a[i];

            while (crc > 255) {
                crc = crc - 255;
            }
            j = j + 2;
        }

        data[data.length-1]=(byte)crc;
        return data;
    }
}
