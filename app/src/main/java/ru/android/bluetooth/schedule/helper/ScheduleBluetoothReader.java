package ru.android.bluetooth.schedule.helper;

import android.content.Context;
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
import ru.android.bluetooth.db.DBHelper;

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
    private DBHelper mDbHelper;

    public ScheduleBluetoothReader(BluetoothMessage mBluetoothMessage,Context context) {
        this.mBluetoothMessage = mBluetoothMessage;
        mValues = new ArrayList<String>();
        mDbHelper = new DBHelper(context);
    }

    private String getDate(Calendar calendar){
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                calendar.get(Calendar.MONTH) + "/" +
                calendar.get(Calendar.YEAR);
    }
    private String createFileName(Calendar startDate, Calendar finishDate){
        String begin = getDate(startDate);
        String finish = getDate(finishDate);
        return begin + "_" + finish;
    }

    public void readSchedule() {
        Calendar startDate = Calendar.getInstance();
        Calendar finishDate = startDate;
        finishDate.add(Calendar.YEAR, 1);
        readSchedule(startDate, finishDate);
    }

    public void readSchedule(Calendar startDate, Calendar finishDate){

        this.startDate = startDate;
        this.finishDate = finishDate;

        try{
            writer = new PrintWriter(Environment.getExternalStorageDirectory() + "/" + "schedule.txt", "UTF-8");
            Log.d(TAG, "path=" + Environment.getExternalStorageDirectory() + "/" + "schedule.txt");
            writer.println(createFileName(startDate, finishDate));
            mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);

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
        mDbHelper.addContact(new OneDayModel(startDate, item));
        Log.d(TAG, item);

        if (!isFinish()){
            mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);
        }else {
            writer.close();
        }

    }

}
