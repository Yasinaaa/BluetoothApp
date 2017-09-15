package ru.android.bluetooth.bluetooth;

import android.util.Log;

/**
 * Created by yasina on 05.08.17.
 */

public class BluetoothCommands {

    private static final String TAG = "BluetoothCommands";

    public static final String GET_TABLE="get table\r\n";
    public static final String RESET = "Reset\r\n";
    public static final String STATUS = "Read Status\r\n";
    public static final String VERSION = "Get Version BT\r\n";
    public static final String SET_TIME = "Set Time=%s:%s:%s\r\n";
    public static final String SET_DATE = "Set Date=%s-%s-%s\r\n";
    public static final String GET_TIME = "Get Time\r\n";
    private static final String SET_NAME = "Set Name=%s\r\n";
    private static final String SET_PASSWORD = "Set Password=%s\r\n";
    public static final String SET_DATA = "Set Data\r\n";
    public static final String DEBUG = "Get AOnOff\r\n";
    public static final String ON = "Rele On\r\n";
    public static final String OFF = "Rele Off\r\n";
    public static final String MANUAL_ON = "Manual On\r\n";
    public static final String MANUAL_OFF = "Manual Off\r\n";

    public final static int REQUEST_ENABLE_BT = 1;
    public final static int MESSAGE_READ = 2;
    public final static int CONNECTING_STATUS = 3;

   /* private static final String COMMAND = "@%s $%s %s $%s %s";

    public static String setCommand(String startDate, String startTime, String startPow, String finshTime, String finishPow){
        return String.format(COMMAND, startDate, startTime, startPow, finshTime, finishPow);
    }*/

    public static String setPassword(String password){
        return String.format(SET_PASSWORD, password);
    }

    public static String setName(String name){
        return String.format(SET_NAME, name);
    }

    public static String setDate(int year, int month, int day){
        String yearStr = String.valueOf(year);
        yearStr = yearStr.substring(yearStr.length() - 2);
        return String.format(SET_DATE, new String[]{ String.valueOf(day), numToStr(month),
                yearStr});
    }

    public static String setTime(int hour, int min){
        return String.format(SET_TIME, new String[]{ numToStr(hour), numToStr(min), "00"});
    }

    private static String numToStr(int num){
        String numStr = String.valueOf(num);
        if(numStr.length() == 1) numStr = "0" + numStr;
        return numStr;
    }

}
