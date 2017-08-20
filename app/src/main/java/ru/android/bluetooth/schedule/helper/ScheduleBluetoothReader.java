package ru.android.bluetooth.schedule.helper;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;

/**
 * Created by yasina on 18.08.17.
 */

public class ScheduleBluetoothReader {

    private final String TAG = ScheduleBluetoothReader.class.getCanonicalName();
    private BluetoothMessage mBluetoothMessage;
    private List<String> mValues;
    private PrintWriter writer;
    private Calendar startDate;
    private Calendar finishDate;

    public ScheduleBluetoothReader(BluetoothMessage mBluetoothMessage) {
        this.mBluetoothMessage = mBluetoothMessage;
        mValues = new ArrayList<String>();
    }

    private void createFile(){
        File file = new File("scccc.txt");
        try{
            writer = new PrintWriter(Environment.getExternalStorageDirectory() + "the-file-name.txt", "UTF-8");
            writer.println("The first line");

        } catch (IOException e) {

        }
    }
    public void readSchedule(Calendar startDate, Calendar finishDate){
        //createFile();
        this.startDate = startDate;
        this.finishDate = finishDate;

        try{
            writer = new PrintWriter(Environment.getExternalStorageDirectory() + "/the-file-name.txt", "UTF-8");
            Log.d(TAG, "path=" + Environment.getExternalStorageDirectory() + "/the-file-name.txt");
            //writer.println("The first line");
            mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);

            /*while(startDate.compareTo(finishDate) <= 0){
                mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);
                startDate.add(Calendar.DAY_OF_YEAR, 1);
            }*/

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public boolean isFinish(){
        if(startDate.compareTo(finishDate) <= 0){
            startDate.add(Calendar.DAY_OF_YEAR, 1);
            return false;
        }
        return true;
    }

    public void addItem(String item){

//        String answer = item.substring(0, item.indexOf("\r\n"));
       // writer.println(answer);
       // Log.d(TAG,answer);
        writer.println(item);
        Log.d(TAG,item);
        if (!isFinish()){
            mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);
        }else {
            writer.close();
        }

    }

}
