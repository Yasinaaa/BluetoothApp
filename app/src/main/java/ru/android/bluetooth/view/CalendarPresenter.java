package ru.android.bluetooth.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

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
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.DateParser;
import ru.android.bluetooth.utils.ActivityHelper;

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
    private Activity mActivity;
    private AlertDialog mDialog;


    public CalendarPresenter(Activity a, BluetoothMessage mBluetoothMessage, CalendarModule.View view) {
        this.mActivity = a;
        this.mContext = a.getApplicationContext();
        this.mBluetoothMessage = mBluetoothMessage;
        this.mView = view;
        init();
    }

    private void init(){
        mBluetoothMessage.setBluetoothMessageListener(this);
        mDateParser = new DateParser(mCurrentDay, mContext);
    }

    private void exportToExcel(String table) {
        final String fileName = "Schedule.xls";
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);

            WritableSheet sheet = workbook.createSheet("schedule", 0);

            try {
                sheet.addCell(new Label(0, 0, "Subject")); // column and row
                sheet.addCell(new Label(1, 0, "Description"));


            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(final TableLayout tableLayout){
        File file = new File(Environment.getExternalStorageDirectory(),"Schedule.xls");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String item;

            boolean isFirstLine = true;
            int i = 0;
            while ((item = br.readLine()) != null) {
                if (item.matches("(.*)=\\d+,\\d+,\\d+")) {
                    String items[];
                    if(StringUtils.countMatches(item, "i") == 2){
                         items = item.split("i");
                    }
                    else {
                        items = new String[1];
                        items[0] = item;
                    }

                    try {
                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.item_schedule_day, null);
                        TextView day = (TextView) view.findViewById(R.id.tv_day);
                        TextView on = (TextView) view.findViewById(R.id.tv_on_time);
                        TextView off = (TextView) view.findViewById(R.id.tv_off_time);

                        for (String it: items) {
                            day.setText(mDateParser.getDate(item.substring(it.indexOf("=") + 1, it.indexOf(","))));
                            on.setText(mDateParser.getTime(item.substring(it.lastIndexOf(",") + 1, it.length())));
                            off.setText(mDateParser.getTime(item.substring(it.indexOf(",") + 1, it.lastIndexOf(","))));

                            tableLayout.addView(view, i);
                            final int finalI = i;
                            final Resources resource = mContext.getResources();
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (selectedItem == finalI) {
                                        view.setBackgroundColor(resource.getColor(R.color.white));
                                        selectedItem = 999;

                                    } else {
                                        view.setBackgroundColor(resource.getColor(R.color.silver));
                                        if (selectedItem != 999) {
                                            tableLayout.getChildAt(selectedItem).
                                                    setBackgroundColor(resource.getColor(R.color.white));
                                        }
                                        selectedItem = finalI;
                                    }


                                }
                            });
                            i++;
                        }

                        ActivityHelper.hideProgressBar(mDialog);
                    } catch (java.lang.Exception e) {
                        ActivityHelper.hideProgressBar(mDialog);
                    }
                }
            }
            br.close();
        }
        catch (IOException e) {
            ActivityHelper.hideProgressBar(mDialog);
        }

    }

    @Override
    public void setTable(TableLayout tableLayout){
       readFile(tableLayout);
    }

    @Override
    public void getSchedule() {
        writeFile();
        mDialog = ActivityHelper.showProgressBar(mActivity, "Считывание расписания");
        mBluetoothMessage.writeMessage(BluetoothCommands.GET_TABLE);
        mBluetoothMessage.writeMessage("ddd\r\n");
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
        if (answer.contains("Not") || answer.contains("command")){
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

            File file = new File(Environment.getExternalStorageDirectory(),"schedule.xls");
            file.createNewFile();

            output = new BufferedWriter(new FileWriter(file));


            //Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
           // Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            final int REQUEST_CODE = 0x11;

            String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            ActivityCompat.requestPermissions(mActivity, permissions, REQUEST_CODE); // without sdk version check

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


    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);

    }

}
