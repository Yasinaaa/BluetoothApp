package ru.android.bluetooth.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.os.Handler;

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
import ru.android.bluetooth.common.date_time.DateParser;
import ru.android.bluetooth.schedule.ScheduleGenerator;
import ru.android.bluetooth.temp.OneFragment;
import ru.android.bluetooth.temp.Temp;
import ru.android.bluetooth.utils.CacheHelper;
import ru.android.bluetooth.utils.DialogHelper;

/**
 * Created by yasina on 17.09.17.
 */

public class CalendarPresenter implements CalendarModule.Presenter,
        BluetoothMessage.BluetoothMessageListener {

    private Context mContext;
    private BluetoothMessage mBluetoothMessage;
    private Writer output = null;
    private CalendarModule.View mView;
    private int selectedItem = 999;
    private DateParser mDateParser;
    private Activity mActivity;
    private AlertDialog mDialog;
    private String mTable;
    final String fileName = "Schedule.xls";
    private int[] onList;
    private int[] offList;
    private String mStatus;
    private CalendarModule.OnItemClicked mOnClick;
    private Handler mHandler;

    public CalendarPresenter(Activity a, BluetoothMessage mBluetoothMessage, CalendarModule.View view,
                             CalendarModule.OnItemClicked onClick) {
        this.mActivity = a;
        this.mContext = a.getApplicationContext();
        this.mBluetoothMessage = mBluetoothMessage;
        this.mDateParser = new DateParser();
        this.mView = view;
        this.mOnClick = onClick;
        init();
    }

    private void init(){
        mHandler = new Handler(Looper.getMainLooper());
        mBluetoothMessage.setBluetoothMessageListener(this);
        onList = new int[366];
        offList = new int[366];
    }

    private void exportToExcel(String table) {
        DialogHelper.changeProgressBarText(mDialog, "Запись расписания в файл");

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
            DateParser mDateParser = new DateParser(Calendar.getInstance(), mContext);

            try {
                String[] textArray = table.split("\n");
                for (int i=0; i<textArray.length; i++){
                    String[] underTextArray;
                    if(StringUtils.countMatches(textArray[i], "i") == 2){
                        underTextArray = textArray[i].split("i");
                    }
                    else {
                        underTextArray = new String[1];
                        underTextArray[0] = textArray[i];
                    }

                    for (int j=0; j<underTextArray.length; j++){
                        try {
                            sheet.addCell(new Label(0, i, mDateParser.getDate()));
                            /*
                            underTextArray[j].
                                    substring(underTextArray[j].indexOf("=") + 1,
                                            underTextArray[j].indexOf(",")))
                             */

                        }catch (java.lang.StringIndexOutOfBoundsException e){

                        }

                        try {
                            //sheet.addCell(new Label(1, i, "00:01"));
                            sheet.addCell(new Label(1, i, mDateParser.getTime(underTextArray[j].substring(
                                    underTextArray[j].lastIndexOf(",") + 1,
                                    underTextArray[j].length()))));
                        }catch (java.lang.StringIndexOutOfBoundsException e){

                        }

                        try{
                            //sheet.addCell(new Label(2, i, "00:01"));
                            sheet.addCell(new Label(2, i, mDateParser.getTime(underTextArray[j].substring(underTextArray[j].
                                            indexOf(",") + 1,
                                    underTextArray[j].lastIndexOf(",")))));
                        }catch (java.lang.StringIndexOutOfBoundsException e){

                        }
                    }

                }

            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            DialogHelper.hideProgressBar(mDialog);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                    .setTitle("Файл сохранен")
                    .setMessage("Сохранен в " + file.getPath())
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            dialogBuilder.show();

            CacheHelper.setSchedulePath(mContext, file.getPath());
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Day> readFile(){

        ArrayList<Day> scheduleMap = new ArrayList<Day>();

        if (mTable != null) {
            String[] textArray = mTable.split("\r\n");

            for (int i = 0; i < textArray.length-1; i++) {

                String[] underTextArray;
                if (StringUtils.countMatches(textArray[i], "i") > 1) {
                    underTextArray = textArray[i].split("i");
                } else {
                    underTextArray = new String[1];
                    underTextArray[0] = textArray[i];
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.hideProgressBar(mDialog);
                    }
                });

                for (int j = 0; j < underTextArray.length; j++) {

                    if (underTextArray[j].matches("(.*)=\\d+,\\d+,\\d+")) {

                        try {

                            String dayStr = underTextArray[j].substring(underTextArray[j].indexOf("=") + 1,
                                    underTextArray[j].indexOf(","));
                            String onNum = underTextArray[j].substring(
                                    underTextArray[j].lastIndexOf(",") + 1, underTextArray[j].length());
                            String offNum = underTextArray[j].substring(underTextArray[j].indexOf(",") + 1,
                                    underTextArray[j].lastIndexOf(","));

                            int idNum = Integer.parseInt(dayStr);
                            onList[idNum] = Integer.parseInt(onNum);
                            offList[idNum] = Integer.parseInt(offNum);

                        } catch (Exception e) {
                            Log.d("d", e.getMessage());
                            DialogHelper.hideProgressBar(mDialog);
                        }
                    }
                }
            }

            Calendar calendar = Calendar.getInstance();
            Calendar monthCalendar;
            int daysOnMonth;
            int daysPast = 0;

            for (int month=0; month<12; month++) {
                monthCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), month, 0);
                daysOnMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                cutTheScheduleByMonth(daysOnMonth, daysPast, scheduleMap);
                daysPast = daysPast + daysOnMonth;
            }
        }
        return scheduleMap;
    }

    private void cutTheScheduleByMonth(int daysCount, int beginDay,
                                       ArrayList<Day> scheduleMap){

        int[] on,off;
        on = Arrays.copyOfRange(onList, beginDay, beginDay + daysCount);
        off = Arrays.copyOfRange(offList, beginDay, beginDay + daysCount);
        if(on != null && off != null)
            scheduleMap.add(new Day(on, off));
    }

    @Override
    public ArrayList<Day> setTable(){
        //tableLayout.removeAllViews();
        //readFile(tableLayout);
        return readFile();
    }

    @Override
    public void setLoadSchedule(){
        mDialog = DialogHelper.showProgressBar(mActivity);
        exportToExcel(mTable);
    }

    @Override
    public void getSchedule() {
        mTable = "";

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDialog = DialogHelper.showProgressBar(mActivity, "Считывание расписания");
            }
        });

        mStatus = BluetoothCommands.GET_TABLE;
        mBluetoothMessage.writeMessage(mActivity,BluetoothCommands.GET_TABLE);
        SystemClock.sleep(1000);
        mBluetoothMessage.writeMessage(mActivity,"ddd\r\n");
    }

    @Override
    public void onResponse(String answer) {
        Log.d("d", answer);
        if(answer.contains("Not") || answer.contains("command")){
            if (mStatus.equals(BluetoothCommands.GET_TABLE)) {

                answer = answer.replaceAll("[Notcomand ]", "");
                mTable += answer;
                mView.onLoadingScheduleFinished();
                DialogHelper.hideProgressBar(mDialog);

            }else if (mStatus.equals(BluetoothCommands.SET_DATA)){

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDialog = DialogHelper.showProgressBar(mActivity, "Запись изменений");
                    }
                });
                mStatus = BluetoothCommands.GET_TABLE;
                mBluetoothMessage.writeMessage(mActivity, BluetoothCommands.GET_TABLE);
                SystemClock.sleep(1000);
                mBluetoothMessage.writeMessage(mActivity,"ddd\r\n");


            }else if(mStatus.equals(BluetoothCommands.SET_DATA + "2")){
                mActivity.finish();
                mActivity.startActivity(mActivity.getIntent());

            } else if (mStatus.equals(BluetoothCommands.GET_TABLE + "2")) {

            }

        }else if(mStatus.equals(BluetoothCommands.GET_TABLE)){
            mTable +=answer;
        }

    }

    @Override
    public void searchDay(String date, CalendarFragment calendarFragment, ViewPager viewPager) {

        TableLayout tableLayout = calendarFragment.getTableLayout();

        for (int i = 0; i < 366; i++){
            View child = tableLayout.getChildAt(i);
            TextView textView = (TextView) child.findViewById(R.id.tv_day);
            if(textView.getText().equals(date)){
                viewPager.scrollTo(0, child.getTop());
                child.setBackgroundColor(mContext.getResources().getColor(R.color.silver));
            }
        }
    }

    public boolean dst = false;

    @Override
    public void generateSchedule(Calendar startDate, Calendar endDate, double latitude, double longitude, int zone) {
        Calendar currentDate = Calendar.getInstance();
        double sunRise;
        double sunSet;
        int d = 0;


        while (startDate.compareTo(endDate) <= 0) {
            currentDate = startDate;

            double JD = ScheduleGenerator.calcJD(startDate);  //OR   JD = Util.calcJD(2014, 6, 1);
            sunRise = ScheduleGenerator.calcSunRiseUTC(JD, latitude, longitude);
            sunSet = ScheduleGenerator.calcSunSetUTC(JD, latitude, longitude);

            Log.d("TAG","day=" + d + " " +
                    ScheduleGenerator.getTimeString(false,sunRise, zone, JD, dst) + " " +
                    ScheduleGenerator.getTimeString(false,sunSet, zone, JD, dst)+ " " +
                    ScheduleGenerator.getTimeString(true,sunRise, zone, JD, dst)+ " " +
                    ScheduleGenerator.getTimeString(true,sunSet, zone, JD, dst)
            );
            /*int zone = Integer.parseInt(mActvTimezone.getText().toString());
            */
            onList[d] = Integer.parseInt(ScheduleGenerator.getTimeString(true, sunRise, zone, JD, dst));
            offList[d] = Integer.parseInt(ScheduleGenerator.getTimeString(true, sunSet, zone, JD, dst));
            d++;

            startDate.add(Calendar.DAY_OF_YEAR, 1);

        }
        if(onList != null & offList != null){
            mTable = "";
            mStatus = BluetoothCommands.SET_DATA+"2";
            mBluetoothMessage.writeMessage(mActivity, onList, offList);
        }
    }

    @Override
    public void saveChanges(int dayOfYear, int dayOfMonth, int on, int off, CalendarFragment calendarFragment) {

        TableLayout tableLayout = calendarFragment.getTableLayout();
        View view = tableLayout.getChildAt(dayOfMonth+1);
        TextView onTv = (TextView) view.findViewById(R.id.tv_on_time);
        TextView offTv = (TextView) view.findViewById(R.id.tv_off_time);
        onTv.setText(mDateParser.getTime(on));
        offTv.setText(mDateParser.getTime(off));
        view.setBackgroundColor(mContext.getResources().getColor(R.color.white_overlay));
        selectedItem = 999;
        calendarFragment.mListOn[dayOfMonth] = on;
        calendarFragment.mListOff[dayOfMonth] = off;

        onList[dayOfYear-1] = on;
        offList[dayOfYear-1] = off;
    }

    @Override
    public void generateSchedule() {

        if(onList != null & offList != null){
            mTable = "";
            mStatus = BluetoothCommands.SET_DATA + "2";
            mBluetoothMessage.writeMessage(mActivity, onList, offList);
        }

    }

    public void setupViewPager(TabLayout tabLayout, final ViewPager viewPager, MonthAdapter adapter) {

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
