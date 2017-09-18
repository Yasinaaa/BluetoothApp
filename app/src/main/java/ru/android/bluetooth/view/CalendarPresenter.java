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
import ru.android.bluetooth.common.DateParser;

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
    private int selectedItem = 999;
    private DateParser mDateParser;


    public CalendarPresenter(Context mContext, BluetoothMessage mBluetoothMessage, CalendarModule.View view) {
        this.mContext = mContext;
        this.mBluetoothMessage = mBluetoothMessage;
        this.mView = view;
        init();
    }

    private void init(){
        mBluetoothMessage.setBluetoothMessageListener(this);
        mDateParser = new DateParser(mCurrentDay, mContext);
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
                        day.setText(mDateParser.getDate(item.substring(item.indexOf("=") + 1, item.indexOf(","))));
                        TextView on = (TextView) view.findViewById(R.id.tv_on_time);

                        TextView off = (TextView) view.findViewById(R.id.tv_off_time);
                        on.setText(mDateParser.getTime(item.substring(item.lastIndexOf(",") + 1, item.length())));
                        off.setText(mDateParser.getTime(item.substring(item.indexOf(",") + 1, item.lastIndexOf(","))));

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
        Log.d("d", answer);
        if(answer != " ") {
            try {
                output.write(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //SEND NOT COMMAND
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



}
