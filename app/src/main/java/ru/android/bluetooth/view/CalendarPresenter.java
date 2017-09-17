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
                        day.setText(item.substring(item.indexOf("=") + 1, item.indexOf(",")));
                        TextView on = (TextView) view.findViewById(R.id.tv_on_time);

                        TextView off = (TextView) view.findViewById(R.id.tv_off_time);
                        on.setText(item.substring(item.lastIndexOf(",") + 1, item.length()));
                        off.setText(item.substring(item.indexOf(",") + 1, item.lastIndexOf(",")));

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
            output = new BufferedWriter(new FileWriter(file));


            //Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
