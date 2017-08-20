package ru.android.bluetooth.schedule.helper;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by dinar on 20.08.17.
 */

public class S2 {
   /* public String doPart2(String p){
        String _so = p.substring(0, 128);
        p = p.substring(_so.length());
        char c = 255;
        do
        {
            _so = _so + c;
        }
        while (_so.length() < 128);
        Log.e("TAG", "so=" + _so);
        return strToPackCRC(_so);
    }*/

   public void writeData(OutputStream mmOutStream, int count) {

       byte[] data = dataToBytes(count);
       try {
           mmOutStream.write(data);
       } catch (IOException e) {
           Log.e("TAG", e.getMessage());
       }
   }

    public void write(OutputStream mmOutStream, int count) {

            String p = "";
            for (int i = 0; i < 366 * 2; i++) {
                // p += numToStrReverse(i + 1);
                p += (i + 1);
            }

            Log.e("TAG", "p=" + p.length());
            // Log.e("TAG", "numToStr(p.length())=" + numToStr(p.length()));

            //String text = strToPackCRC(numToStr(p.length()));
            //String text = strToPackCRC(null);
            //Log.e("TAG", "text=" + text);
            //String s = "Set Data\r" + text;
            //Log.e("TAG", "sss=" + s);
            byte[] a = strToPackCRC(numToStr(p.length()), count);
            try {
                //mmOutStream.write("Set Data\r\n4104010".getBytes());
                //mmOutStream.write(s.getBytes());

                mmOutStream.write("Set Data\r".getBytes());
                mmOutStream.write(a);
                // mmOutStream.write(doPart2(p).getBytes());

                // mmOutStream.close();
            } catch (IOException e) {
                Log.e("TAG", e.getMessage());
            }

    }

    private byte[] dataToBytes(int count){
        byte one = (byte)128;

        byte[] two = new byte[128];
        Arrays.fill(two, (byte)1);
        //two[0] = (byte)count;
        //byte three = crc(two);
        byte three = (byte)count;

        byte[] combined = new byte[2 + two.length];
        System.arraycopy(two,0,combined,1,two.length);
        combined[0] = one;
        combined[combined.length - 1] = three;
        return combined;
    }

    private byte crc(byte[] two){
        int crc = 1;

        for (int i=0; i<two.length; i++){
            crc += two[i];
            if(crc > 255){
                crc -= 255;
            }
        }
        Log.d("S2", "crc=" + (int)crc);
       //crc += two[0];
        return (byte)crc;
    }

    private byte[] dataToBytes(int count, String data, int crc){
        byte one = (byte)count;

        int d1 = (int)data.charAt(0);
        int d2 = (int)data.charAt(1);
        byte[] two = new byte[2];
        two[0] = (byte)5;
        two[1] = (byte)5;


        byte three = (byte) (two[0] + two[1] + 2);


        byte[] combined = new byte[2 + two.length];
        System.arraycopy(two,0,combined,1,two.length);
        combined[0] = one;
        combined[combined.length - 1] = three;
        return combined;
    }


    private byte[] strToPackCRC(String s, int count)
    {
        char[] _so = null;
        byte[] combined = null;
        int crc = 0;

        if (s != null)
        {
            _so = (s.length() + s).toCharArray();

        }
        else
        {
            return null;
        }

        return dataToBytes(s.length(), s, count);
    }

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
