package ru.android.bluetooth.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;

/**
 * Created by yasina on 17.09.17.
 */

public class CalendarPresenter implements CalendarModule.Presenter,
        BluetoothMessage.BluetoothMessageListener {

    public static final String PREF_BEGIN_DAY = "begin_day_pref";

    private Context mContext;
    private BluetoothMessage mBluetoothMessage;
    private Writer output = null;
    private CalendarModule.View mView;
    private Calendar mCurrentDay = null;
    private int selectedItem = 999;


    public CalendarPresenter(Context mContext, BluetoothMessage mBluetoothMessage, CalendarModule.View view) {
        this.mContext = mContext;
        this.mBluetoothMessage = mBluetoothMessage;
        this.mView = view;
        init();
    }

    private void init(){
        mBluetoothMessage.setBluetoothMessageListener(this);
    }

    private void readFile(final TableLayout tableLayout){
        File file = new File(Environment.getExternalStorageDirectory(),"schedule.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String item;

            boolean isFirstLine = true;
            int i = 0;
            while ((item = br.readLine()) != null) {
                if (item.matches("(.*)=\\d+,\\d+,\\d+")) {
                    try {
                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.item_schedule_day, null);
                        TextView day = (TextView) view.findViewById(R.id.tv_day);
                        day.setText(getDate(item.substring(item.indexOf("=") + 1, item.indexOf(","))));
                        TextView on = (TextView) view.findViewById(R.id.tv_on_time);

                        TextView off = (TextView) view.findViewById(R.id.tv_off_time);
                        on.setText(getTime(item.substring(item.lastIndexOf(",") + 1, item.length())));
                        off.setText(getTime(item.substring(item.indexOf(",") + 1, item.lastIndexOf(","))));

                        tableLayout.addView(view, i);
                        final int finalI = i;
                        final Resources resource = mContext.getResources();
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(selectedItem == finalI){
                                    view.setBackgroundColor(resource.getColor(R.color.white));
                                    selectedItem = 999;

                                }else{
                                    view.setBackgroundColor(resource.getColor(R.color.silver));
                                    if(selectedItem != 999){
                                        tableLayout.getChildAt(selectedItem).setBackgroundColor(resource.getColor(R.color.white));
                                    }
                                    selectedItem = finalI;
                                }


                            }
                        });
                        i++;

                    } catch (java.lang.Exception e) {

                    }
                }
            }
            br.close();
        }
        catch (IOException e) {

        }

    }

    private String getDate(String dayNum){
        //Calendar newCalendar = mCurrentDay;
        if(mCurrentDay == null){
            mCurrentDay = Calendar.getInstance();
            String result = setCorrectDateView(mCurrentDay);
            saveBeginDay(result);
            return result;
            //newCalendar = mCurrentDay;
        }else {
            //newCalendar.add(Calendar.DATE, Integer.parseInt(dayNum));
            mCurrentDay.add(Calendar.DATE, 1);
            return setCorrectDateView(mCurrentDay);
        }
    }

    private String setCorrectDateView(Calendar calendar){
        int month = mCurrentDay.get(Calendar.MONTH) + 1;
        return setZeros(mCurrentDay.get(Calendar.DAY_OF_MONTH)) + "."
                + setZeros(month) + "."
                + setZeros(mCurrentDay.get(Calendar.YEAR));
    }

    private String getTime(String time){
        int timeMin = Integer.parseInt(time);
        String hour = setZeros(String.valueOf(timeMin / 60));
        String min = setZeros(String.valueOf(timeMin % 60));
        return hour + ":" + min;
    }

    private String setZeros(String time){
        if(time.length() == 1){
            time = "0" + time;
        }
        return time;
    }

    private String setZeros(int time){
        return setZeros(String.valueOf(time));
    }

    @Override
    public void setTable(TableLayout tableLayout){
       readFile(tableLayout);
    }

    @Override
    public void getSchedule() {
        writeFile();
        mBluetoothMessage.writeMessage(BluetoothCommands.GET_TABLE);
    }

    @Override
    public void onResponse(String answer) {
        if(answer != " ") {
            try {
                output.write(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (answer.contains("is=365")){
            try {
                output.close();
                mView.onLoadingScheduleFinished();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeFile(){
        try {

            File file = new File(Environment.getExternalStorageDirectory(),"schedule.txt");
            file.createNewFile();
            output = new BufferedWriter(new FileWriter(file));


            //Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void searchDay(String date, TableLayout tableLayout, NestedScrollView nestedScrollView) {

        for (int i = 0; i < 365; i++){
            View child = tableLayout.getChildAt(i);
            TextView textView = (TextView) child.findViewById(R.id.tv_day);
            if(textView.getText().equals(date)){
                nestedScrollView.scrollTo(0, child.getTop());
                child.setBackgroundColor(mContext.getResources().getColor(R.color.silver));
            }
        }
    }

    /*public Calendar setStringToDate(String date){
        String parts[] = date.split("\\.");

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar;
    }*/

    @Nullable
    public String getBeginDate() {
        if (mContext == null) return null;

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        return sp.getString(PREF_BEGIN_DAY, "");
    }

    public void saveBeginDay(@Nullable String date) {
        if (mContext == null || date == null) return;
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        sp.edit().putString(PREF_BEGIN_DAY, date).apply();
    }

}
