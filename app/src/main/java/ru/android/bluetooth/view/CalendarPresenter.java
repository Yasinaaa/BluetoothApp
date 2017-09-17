package ru.android.bluetooth.view;

import android.content.Context;
import android.os.Environment;
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

    private Context mContext;
    private BluetoothMessage mBluetoothMessage;
    private Writer output = null;
    private CalendarModule.View mView;
    private Calendar mCurrentDay = null;

    public CalendarPresenter(Context mContext, BluetoothMessage mBluetoothMessage, CalendarModule.View view) {
        this.mContext = mContext;
        this.mBluetoothMessage = mBluetoothMessage;
        this.mView = view;
        init();
    }

    private void init(){
        mBluetoothMessage.setBluetoothMessageListener(this);
        writeFile();
    }

    private void readFile(TableLayout tableLayout){
        File file = new File(Environment.getExternalStorageDirectory(),"schedule.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String item;

            boolean isFirstLine = true;
            int i = 0;
            while ((item = br.readLine()) != null) {
                if (item.matches("(.*)=\\d+,\\d+,\\d+")) {
                    try {
                        TableRow tableRow = new TableRow(mContext);
                        tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));

                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.item_schedule_day, null);
                        TextView day = (TextView) view.findViewById(R.id.tv_day);
                        day.setText(getDate(item.substring(item.indexOf("=") + 1, item.indexOf(","))));
                        TextView on = (TextView) view.findViewById(R.id.tv_on_time);

                        TextView off = (TextView) view.findViewById(R.id.tv_off_time);
                        on.setText(getTime(item.substring(item.lastIndexOf(",") + 1, item.length())));
                        off.setText(getTime(item.substring(item.indexOf(",") + 1, item.lastIndexOf(","))));

                        tableLayout.addView(view, i);
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
            //newCalendar = mCurrentDay;
        }else {
            //newCalendar.add(Calendar.DATE, Integer.parseInt(dayNum));
            mCurrentDay.add(Calendar.DATE, 1);
        }
        return setZeros(mCurrentDay.get(Calendar.DAY_OF_MONTH)) + "."
                + setZeros(mCurrentDay.get(Calendar.MONTH)) + "."
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
}
