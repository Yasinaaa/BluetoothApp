package ru.android.bluetooth.schedule.helper;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by yasina on 18.08.17.
 */

public class ScheduleFileReader {

    private List<OneDayModel> mOneDayModelList;
    private Calendar beginDate, endDate;

    public ScheduleFileReader() {
        mOneDayModelList = new ArrayList<OneDayModel>();
    }

    public void fillListWithSchedule(){

    }

    private void setBeginDate(String text){
        beginDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        String[] dates = text.split("_");
        StringTokenizer stringTokenizer = new StringTokenizer(dates[0], "/");
        beginDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(stringTokenizer.nextToken()));
        beginDate.set(Calendar.MONTH, Integer.parseInt(stringTokenizer.nextToken()) - 1);
        beginDate.set(Calendar.YEAR, Integer.parseInt(stringTokenizer.nextToken()));

    }

    private void readFile(){
        File file = new File(Environment.getExternalStorageDirectory(),"schedule.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine){
                    setBeginDate(line);
                    isFirstLine = false;
                }
                if (line.matches("i=\\d+,\\d+,\\d+")){

                }else {

                }
            }
            br.close();
        }
        catch (IOException e) {

        }

    }

}
