package ru.android.bluetooth.schedule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;

/**
 * Created by itisioslab on 08.08.17.
 */

public class ScheduleGeneratorActivity extends AppCompatActivity implements BluetoothMessage.BluetoothMessageListener {

    private final String TAG = ScheduleGeneratorActivity.class.getName();

    int timerBehaviour = 0;
    public double JD = 0;
    public int zone = 0; // Seattle time Zone
    public double latitude = 55.7887; // Seattle lat
    public double longitude = 49.1221; // Seattle lon
    public boolean dst = false; // Day Light Savings

    private BluetoothMessage mBluetoothMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.YEAR, 1);
        //generateSchedule(Calendar.getInstance(), currentDate, latitude, longitude);
        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);



        //mBluetoothMessage.writeMessage( "[FILE_W]@10.08.2017 $13:45:00 %0 $15:00:00 %100;");
        //mBluetoothMessage.writeMessage( "[FILE_R];");
        mBluetoothMessage.writeMessage( "@10.08.2017 $12:02:00 %0 $12:03:00 %100;\r\n");
       // mStatus = BluetoothCommands.SET_DATA;
       // mBluetoothMessage.writeMessage("[" + BluetoothCommands.setCommand("10.08.2017","11:44:00", "%0", "11:45:00", "%100") + "];");

        //Log.d(TAG, "BluetoothCommands.setComma = " + BluetoothCommands.setCommand("10.08.2017","11:16:00", "%0", "11:17:00", "%100"));
       // mBluetoothMessage.writeMessage(BluetoothCommands.SET_DATA + " " + BluetoothCommands.setCommand("10.08.2017","11:26:00", "%0", "11:27:00", "%100"));
        Log.d(TAG, "1");
        //mBluetoothMessage.writeMessage("Set Data " + BluetoothCommands.setCommand("10.08.2017","11:28:00", "%0", "11:29:00", "%100") + "\r\n");
        //Log.d(TAG, "1");
       // mBluetoothMessage.writeMessage(BluetoothCommands.setCommand("10.08.2017","11:13", "0", "11:14", "100"));
    }

    private void generateSchedule(Calendar startDate, Calendar endDate, double latitude, double longitude){
        Calendar currentDate = Calendar.getInstance();
        double sunRise;
        double sunSet;
        int d = 0;
        while(startDate.compareTo(endDate) <= 0){
            currentDate = startDate;

            JD = ScheduleGenerator.calcJD(startDate);  //OR   JD = Util.calcJD(2014, 6, 1);
            sunRise = ScheduleGenerator.calcSunRiseUTC(JD, latitude, longitude);
            sunSet = ScheduleGenerator.calcSunSetUTC(JD, latitude, longitude);

            //date.ToString().Remove(10,date.ToString().Length - 10)

           // String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp",ScheduleGenerator.getDateTime(false,sunRise, zone, JD, dst).getSelectedDay().getTime());
            d++;
            Log.d(TAG,"day=" + d + " " +
                    ScheduleGenerator.getTimeString(false,sunRise, zone, JD, dst) + " " +
                    ScheduleGenerator.getTimeString(false,sunSet, zone, JD, dst)+ " " +
                    ScheduleGenerator.getTimeString(true,sunRise, zone, JD, dst)+ " " +
                    ScheduleGenerator.getTimeString(true,sunSet, zone, JD, dst)
            );
            startDate.add(Calendar.DAY_OF_YEAR, 1);


        }
    }

    @Override
    public void onResponse(String answer) {
        Log.d(TAG, answer);
    }
}
