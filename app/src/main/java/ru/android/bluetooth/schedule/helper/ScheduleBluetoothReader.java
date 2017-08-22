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

    private String getDate(Calendar calendar){
        return calendar.get(Calendar.DAY_OF_MONTH) + "_" +
                calendar.get(Calendar.MONTH) + "_" +
                calendar.get(Calendar.YEAR);
    }
    private String createFileName(Calendar startDate, Calendar finishDate){
        String begin = getDate(startDate);
        String finish = getDate(finishDate);
        return begin + "__" + finish + ".txt";
    }

    public void readSchedule(Calendar startDate, Calendar finishDate){

        this.startDate = startDate;
        this.finishDate = finishDate;

        try{
            writer = new PrintWriter(Environment.getExternalStorageDirectory() + "/" +
                    createFileName(startDate, finishDate), "UTF-8");
            Log.d(TAG, "path=" + Environment.getExternalStorageDirectory() + "/" +
                    createFileName(startDate, finishDate));
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

        if(item.contains(" ")) {
            item = item.substring(0, item.indexOf(" "));
        }
        writer.println(item);
        Log.d(TAG, item);

        if (!isFinish()){
            mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);
        }else {
            writer.close();
        }

    }

}
