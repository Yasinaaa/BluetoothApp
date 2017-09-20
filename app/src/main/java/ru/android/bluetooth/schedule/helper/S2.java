package ru.android.bluetooth.schedule.helper;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yasina on 20.08.17.
 */

public class S2 {

    byte b, b1;
    //TODO: NEED
    public void write(OutputStream mmOutStream, int[] lisOn, int[] listOff) {

        try {

            byte[] array = new byte[]{2, 5, (byte)184, (byte)191};
           // byte[] array = new byte[]{2, 6, 0, 8};
           // array = strToPackCRC(mmOutStream,numToStr(p.length()));
           // byte[] answer = get128Array(130);

         //   byte[] answer2  = get128Array(130);
          //  byte[] answer3  = get128Array(58);

            int[] combined = combineBytes(lisOn, listOff);
            ArrayList<int[]> numbers = divideTo128ByteElements(combined);
            ArrayList<byte[]> numsToBytesArray = new ArrayList<byte[]>();
            for (int i = 0; i< numbers.size(); i++){
                numsToBytesArray.add(getValues(numbers.get(i)));
            }

            mmOutStream.write("Set Data\r".getBytes());
            mmOutStream.write(array);

            for (int i=0; i<numsToBytesArray.size();i++) {
                SystemClock.sleep(1000);
                mmOutStream.write(numsToBytesArray.get(i));
            }
           /* SystemClock.sleep(1000);
            mmOutStream.write(answer3);
            mmOutStream.write("d\r\n".getBytes());*/

        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }
       // return p;
    }

    public int[] combineBytes(int[] one, int[] two) {
        int[] combined = new int[one.length + two.length];
        System.arraycopy(one,0,combined,0,one.length);
        System.arraycopy(two,0,combined,one.length,two.length);
        return combined;
    }

    private ArrayList<int[]> divideTo128ByteElements(int[] array){
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

    private byte[] getValues(int[] a){
        //count = 64
        byte[] data = new byte[a.length*2  + 2];
        data[0] = (byte)(data.length - 2);
        int crc = data.length - 2;

        byte[] value;
        int j = 0;
        for (int i=0; i<a.length; i++){

            value = BigInteger.valueOf(a[i]).toByteArray();
            if (value.length == 1){
                //data[j + 1] = 0;
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

        Log.d("tg", "crc=" + crc);
        return data;
    }
    //TODO: NEED
    private byte[] get128Array(int count){
        byte[] data = new byte[count];
        data[0] = (byte)(data.length - 2);
        int crc = data.length - 2;
        //int num = 129;
        /*data[1]= 5;
        data[2]= 0;*/
        /*data[1] = 3;
        data[2] = -94;
        crc += 3;
        crc += -94;*/

        /*byte[] f = BigInteger.valueOf(930).toByteArray();
        data[1] = -94;
        data[2] = 3;
        crc += 930;
        if (crc > 255) {
            crc = crc - 255;
        }*/

        for (int i=3; i<data.length-1; i=i+2){
            data[i] = 1;
            data[i+1] = 0;
            crc += 1;
            if (crc > 255) {
                crc = crc - 255;
            }
        }
        // data = new byte[]{8, 0, 1, 0, 1, 0, 1, 0, 1, 12};
        data[data.length-1]=(byte)crc;
        return data;
    }

   /* byte[] array;
    private byte[] strToPackCRC(OutputStream mmOutStream, String s)
    {
        char[] _so = null;
        byte[] combined = null;
        int crc = s.length();

        if (s != null)
        {
            _so = s.toCharArray();
            for (int i=0; i< _so.length; i++){
                try {
                    crc += Integer.parseInt(_so[i]+"");
                }catch (NumberFormatException e){
                    crc += (int)_so[i];
                }
                if(crc > 255){
                    crc -= 255;
                }
            }

        }
        else
        {
            return null;
        }

        array = new byte[]{2, 5, (byte)180, (byte)187};
        //array = new byte[]{2, 5, 0, 7};
        return array;
    }*/

    private String numToStrReverse(int n){
       String s = String.valueOf((char)(n%256));
       n=n/256;
       return s + String.valueOf((char)n%256);
   }

    private String numToStr(int n){
        String s= String.valueOf((char)(n%256));
        n=n/256;
        return String.valueOf((char)n%256) + s;
    }

}
