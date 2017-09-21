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
    public static final String SET_NAME = "Set Name=%s\r\n";
    public static final String SET_PASSWORD = "Set Password=%s\r\n";
    public static final String SET_DATA = "Set Data\r\n";
    public static final String DEBUG = "Get AOnOff\r\n";
    public static final String ON = "Rele On\r\n";
    public static final String OFF = "Rele Off\r\n";
    public static final String MANUAL_ON = "Manual On\r\n";
    public static final String MANUAL_OFF = "Manual Off\r\n";

    /*public static final int GET_TABLE_NUM = 0;
    public static final int RESET_NUM = 1;
    public static final int STATUS_NUM = 2;
    public static final int VERSION_NUM = 3;
    public static final int SET_TIME_NUM = 4;
    public static final int SET_DATE_NUM = 5;
    public static final int GET_TIME_NUM = 6;
    private static final int SET_NAME_NUM = 7;
    private static final int SET_PASSWORD_NUM = 8;
    public static final int SET_DATA_NUM = 9;
    public static final int DEBUG_NUM = 10;
    public static final int ON_NUM = 11;
    public static final int OFF_NUM = 12;
    public static final int MANUAL_ON_NUM = 13;
    public static final int MANUAL_OFF_NUM = 14;
    /*public final static int REQUEST_ENABLE_BT = 1;

   */
    public final static int MESSAGE_READ = 2;
    public final static int CONNECTING_STATUS = 99;

   /* private static final String COMMAND = "@%s $%s %s $%s %s";

    public static String setCommand(String startDate, String startTime, String startPow, String finshTime, String finishPow){
        return String.format(COMMAND, startDate, startTime, startPow, finshTime, finishPow);
    }*/

   /* public static int getStatusNum(String message){
        switch (message){
            case GET_TABLE:
                return GET_TABLE_NUM;

            case RESET:
                return RESET_NUM;

            case STATUS:
                return STATUS_NUM;

            case VERSION:
                return VERSION_NUM;

            case SET_TIME:
                return SET_TIME_NUM;

            case SET_DATE:
                return SET_DATE_NUM;

            case GET_TIME:
                return GET_TIME_NUM;

            case SET_NAME:
                return SET_NAME_NUM;

            case SET_PASSWORD:
                return SET_PASSWORD_NUM;

            case SET_DATA:
                return SET_DATA_NUM;

            case DEBUG:
                return DEBUG_NUM;

            case ON:
                return ON_NUM;

            case OFF:
                return OFF_NUM;

            case MANUAL_ON:
                return MANUAL_ON_NUM;

            case MANUAL_OFF:
                return MANUAL_OFF_NUM;
        }
        return -99;
    }
*/
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
