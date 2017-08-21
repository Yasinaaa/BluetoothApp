package ru.android.bluetooth.schedule.helper;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Created by dinar on 20.08.17.
 */

public class S {

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
    public void part2(OutputStream mmOutStream, String p, int crc){
        if(p.length()>0){
            String so = p.substring(0, 365);
            //String so2 = p.substring(p.length()/2 + 1, p.length());
           // byte[] answer = myCRC(mmOutStream,so, crc);
            //byte[] answer2 = myCRC(so2);

        try {
            byte[] answer = new byte[]{2,0,1,3};
              mmOutStream.write(answer);
            //mmOutStream.write(answer2);
        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }
        }
    }

    private byte[] myCRC(OutputStream mmOutStream,String data, int crc2){
        int crc = 0;
        int[] _so = new int[data.length()];


        if (data != null)
        {
            char[] temp = data.toCharArray();
            for(int i=0; i< 128;i++){
                _so[i] = 10;
            }

           // crc += data.length();
            crc += 128;

            for (int i=0; i< _so.length; i++){
                crc = crc + _so[i];
                if(crc > 255){
                    crc -= 255;
                }
            }

        }
        Log.d("d", "crc=" + crc);

        return answer(mmOutStream,_so, (byte)crc);
    }

    private byte[] answer(OutputStream mmOutStream, int[] array, byte crc){
        byte[] combined = new byte[1 + array.length + 1];
        byte[] chars = new byte[array.length];
        for (int i=0; i< chars.length; i++){
            chars[i] = (byte) array[i];
        }
        System.arraycopy(chars,0,combined,1, chars.length);
        combined[0] = (byte)array.length;
        combined[combined.length - 1] = (byte)crc;

        return combined;
    }
    private byte myCRC(int[] data){
        int crc = 0;
        int[] _so;

        if (data != null)
        {
            _so = new int[1 + data.length];
            System.arraycopy(data, 0, _so, 1, data.length);
            _so[0] = data.length;

            for (int i=0; i< _so.length; i++){
                crc +=  _so[i];
                if(crc > 255){
                    crc -= 255;
                }
            }

        }
        Log.d("d", "crc=" + crc);
        return (byte)(crc & 0xff);
    }

    private byte[] arrayIntToByte(int[] data){
        byte[] array = new byte[data.length];
        for (int i=0; i< data.length;i++){
            array[i] = (byte)data[i];
        }
        return array;
    }
    public void part2(OutputStream mmOutStream, int[] data){
        byte one = (byte) data.length;

        /*ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);
        byte[] two = byteBuffer.array();*/
        byte[] two = arrayIntToByte(data);

        byte three = myCRC(data);

        byte[] combined = new byte[1 + two.length + 1];
        System.arraycopy(two,0,combined,1, two.length);
        combined[0] = one;
        combined[combined.length - 1] = three;

        try {
            mmOutStream.write(combined);
        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }
    }

    public void write(OutputStream mmOutStream, int count) {

        String p = "";
        for (int i = 0; i < 366 * 2; i++) {
            p += numToStrReverse(i + 1);
            //p += (i + 1);
        }

        Log.e("TAG", "p=" + p.length());
        Log.e("TAG", "numToStr(p.length())=" + numToStr(p.length()));

        //String text = strToPackCRC(mmOutStream,numToStr(p.length()));
        //String text = strToPackCRC(null);
        //Log.e("TAG", "text=" + text);
        //String s = "Set Data\r" + text;
        //Log.e("TAG", "sss=" + s);
        //byte[] a = strToPackCRC(numToStr(p.length()), count);
        try {
            // TODO:
            String n184 = Integer.toHexString(184);
            byte[] array = new byte[]{0x02, 0x05, (byte)0xB8, (byte)0xBF};
            mmOutStream.write("Set Data\r".getBytes());
            mmOutStream.write(array);

        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }

    }
}
