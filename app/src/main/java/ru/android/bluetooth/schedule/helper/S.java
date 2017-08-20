package ru.android.bluetooth.schedule.helper;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Created by yasina on 10.08.17.
 */

public class S {

    public String doPart2(String p){
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
    }
    public void write(OutputStream mmOutStream, int count) {

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
        String p = "";
        for (int i = 0; i < 366 * 2; i++) {
            p += numToStrReverse(i + 1);
        }

        Log.e("TAG", "p=" + p);
        Log.e("TAG", "numToStr(p.length())=" + numToStr(p.length()));
        String text = strToPackCRC(numToStr(p.length()));
        Log.e("TAG", "text=" + text);
        String s = "Set Data\r" + text;
        try {
            //mmOutStream.write("Set Data\r\n4104010".getBytes());
            //mmOutStream.write(s.getBytes());
            byte[] a = temp();

            mmOutStream.write(a);
           // mmOutStream.write(doPart2(p).getBytes());

           // mmOutStream.close();
        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }
    }

    private byte[] temp(){
        byte[] setData = "Set Data\r".getBytes();
        byte one = (byte)2;
        byte[] two = ByteBuffer.allocate(4).putInt(1040).array();
        byte three =(byte)249;

        //[] n = "\n".getBytes();

        byte[] combined = new byte[setData.length + 2 + two.length ];//+ n.length

        System.arraycopy(setData,0,combined,0,setData.length);
        System.arraycopy(two,0,combined,setData.length+1,two.length);
        //System.arraycopy(n,0,combined,setData.length + two.length + 2,n.length);
        combined[setData.length] = one;
        combined[combined.length - 1] = three;
        //combined[combined.length - 2] = three;

        return combined;
    }

    private String bytesToString(byte[] bytes){
        String answer = "";
        for(byte b: bytes){
            answer += b;
        }
        return answer;
    }

    private byte[] newBytesWithLenght(int lenght, byte[] bytes){
        byte[] combined = new byte[1 + bytes.length];
        combined[0] = (byte) lenght;
        System.arraycopy(bytes,0,combined,1,bytes.length);
        return combined;
    }

    private String[] newStringsWithLenght(String[] strings){
        String[] combined = new String[strings.length + 1];
        System.arraycopy(strings,0,combined,1,strings.length);
        combined[0] = strings.length + "";
        return combined;
    }

    private char[] newStringsWithLenght(char[] strings){
        char[] combined = new char[strings.length + 1];
        System.arraycopy(strings,0,combined,1,strings.length);
        char l = (char)strings.length;
        combined[0] = l;
        return combined;
    }

    private String[] getArrayOfString(String s){
        Log.d("TAG", "s.length()" + s.length());
        String[] array = new String[s.length()];
        for (int i=0; i<s.length(); i++){
            array[i] = s.substring(i, i + 1);
        }
        return array;
    }


    private String strToPackCRC(String s)
    {
        char[] _so = null;
        //int crc = 0;
        int crc = 0;
        byte[] combined = null;

        if (s != null)
        {

            //_so = newStringsWithLenght(getArrayOfString(s));
            _so = (s.length() + s).toCharArray();

            for (int i = 0; i <_so.length; i++)
            {
                try {
                    //crc = crc + Integer.parseInt(_so[i]);
                    crc += (int)_so[i];
                    if(crc > 255){
                        crc = crc - 255;
                    }
                }catch (NumberFormatException e) {

                }
            }
        }
        else
        {
            return null;
        }
        Log.d("ff", "crc__="+crc);
        String answer = Arrays.toString(_so) + crc;
        byte[] d = ByteBuffer.allocate(4).putInt(s.length()).array();


        //return s.length() + s + String.valueOf(crc);
        return s.length() + s + (char)crc;
    }
    /*private String strToPackCRC(String[] s)
    {
        String[] _so = null;
        int crc = 0;
        byte[] combined = null;

        if (s.length > 0)
        {
            _so = newStringsWithLenght(s.length,s);

            for (int i = 0; i <_so.length; i++)
            {
                crc = crc + Integer.parseInt(_so[i]);
                if (crc > 255) {
                    crc = crc - 255;
                }
            }
        }
        else
        {
            return null;
        }
        Log.d("ff", "crc__="+crc);
        String answer = Arrays.toString(_so) + String.valueOf(crc);

        return Arrays.toString(_so) + String.valueOf(crc);
    }*/
    /*private String strToPackCRC(byte[] s)
    {
        String _so;
        byte crc = 0;
        byte[] combined = null;

        if (s.length > 0)
        {
            _so = s.length + bytesToString(s);
            Log.d("ff", "_so="+_so);

            combined = newBytesWithLenght(s.length, s);
            for (int i = 0; i <_so.length(); i++)
            {
                crc = (byte)(crc + combined[i]);
                Log.d("ff", "crc="+crc);
                if (crc > 255) {
                    crc = (byte)(crc - 255);
                }
            }
        }
        else
        {
            _so = "";
            crc = 0;
        }
        Log.d("ff", "crc__="+crc);
        return bytesToString(combined) + String.valueOf(crc);
    }*/


    private String numToStrReverse(int n){
        String s=n%256 + "";
        n=n/256;
        s = s + n%256;
        // Log.d("T","Reverse=" + s);
        return s;
    }

    private String numToStr(int n){
        String s=n%256 + "";
        n=n/256;
        s = n%256+s;
        // Log.d("T", s);
        return s;
    }
  /* private String numToStrReverse(int n){
       char s=(char)(n%256);
       n=n/256;
       return s + (char)(n%256) + "";
       // Log.d("T","Reverse=" + s);

   }

    private String numToStr(int n){
        char s=(char)(n%256);
        n=n/256;
        char c = (char)(n%256);
        return c + s + "";
        // Log.d("T", s);

    }*/

}
