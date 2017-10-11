package ru.android.bluetooth.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.date_time.DateParser;
import ru.android.bluetooth.common.date_time.DateTimeClickListener;
import ru.android.bluetooth.common.date_time.DateTimeView;
import ru.android.bluetooth.main.helper.ResponseView;
import ru.android.bluetooth.main.helper.ScheduleLoading;
import ru.android.bluetooth.utils.CacheHelper;
import ru.android.bluetooth.utils.DialogHelper;

/**
 * Created by yasina on 22.08.17.
 */

public class MainPresenter implements DateTimeView{

    private final String TAG = "MainPresenter";
    private Activity mActivity;
    private Context mContext;
    private BluetoothMessage mBluetoothMessage;
    private DateTimeClickListener mDateTimeClickListener;
    private DateParser mDateParser;
    private File mFile;
    private String mTable = "";
    private String mThisTextNeedToSetTextView;


    public MainPresenter(Activity activity, BluetoothMessage bluetoothMessage) {
        this.mActivity = activity;
        this.mContext = mActivity.getApplicationContext();
        this.mBluetoothMessage = bluetoothMessage;
        mDateParser = new DateParser();
        mDateTimeClickListener = new DateTimeClickListener(this, mDateParser);
    }

    public void setScheduleFilePath(EditText mEtScheduleName){
        String scheduleFile = CacheHelper.getSchedulePath(mContext);
        if(!scheduleFile.equals("")){
            mEtScheduleName.setVisibility(View.VISIBLE);
            mEtScheduleName.setText(scheduleFile);
        }else {
            mEtScheduleName.setVisibility(View.GONE);
        }
    }

    public void setDateTimeClickListeners(Button timeBtn, Button dateBtn){

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog();
                mDateTimeClickListener.setDateClickListener(mActivity);
            }
        });

        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog();
                mDateTimeClickListener.setTimeClickListener(mActivity);
            }
        });

    }

    @Override
    public void sendDateMessage(String thisTextNeedToSetTextView, int year, int month, int day) {
        this.mThisTextNeedToSetTextView = thisTextNeedToSetTextView;
        mBluetoothMessage.setMessage(mTable, mActivity, BluetoothCommands.SET_DATE, BluetoothCommands.setDate(year, month+1, day), true);
    }

    @Override
    public void sendTimeMessage(String thisTextNeedToSetTextView, int hour, int min) {
        this.mThisTextNeedToSetTextView = thisTextNeedToSetTextView;
        mBluetoothMessage.setMessage(mTable, mActivity, BluetoothCommands.SET_TIME, BluetoothCommands.setTime(hour, min), true);
    }

    public void parseStatus(String status, TextView mTvDate, TextView mTvTime){
        String[] parameters = status.replaceAll("\r","").split("\n");
        for(String s: parameters){
            if(!s.matches(".*[a-zA-Z]+.*")){
                getTime(s, mTvDate, mTvTime);
            }
        }
    }

    private void getTime(String data, TextView mTvDate, TextView mTvTime){
        String[] allData = data.split(" ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm:ss", Locale.ENGLISH);
        try {
            Date result = dateFormat.parse(data);
            String s = dateFormat.format(result);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(result);
            mTvDate.setText(mDateParser.setZeros(calendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                    mDateParser.setZeros((calendar.get(Calendar.MONTH) + 1)) + "-" +
                    mDateParser.setZeros(calendar.get(Calendar.YEAR)));
            /*

            mTvTime.setText(mDateParser.setZeros(result.getHours()) + ":" +
                    mDateParser.setZeros(result.getMinutes()) + ":" +
                    mDateParser.setZeros(result.getSeconds()));*/
            //mTvDate.setText(" " + allData[0].trim());
            mTvTime.setText(allData[1]);

        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    public interface ResponseParseView{
        void nextJob(String mTable);
    }

    public void parseResponse(boolean isFirstOpen, String answer, ResponseParseView responseView){
        if (answer.contains("Not") || answer.contains("command")) {
            if(!isFirstOpen) DialogHelper.hideProgressBar(mBluetoothMessage.mDialog);
            answer = answer.replaceAll("[Notcomand ]", "");
            mTable += answer;
            //mBluetoothMessage.iAmBusy = false;
            responseView.nextJob(mTable);


        }else if(answer.equals("")){
            if(!isFirstOpen) DialogHelper.hideProgressBar(mBluetoothMessage.mDialog);
        }else {
            mTable += answer;
        }
    }

    public void parseResponse(String answer, ResponseParseView responseView){
        parseResponse(false, answer, responseView);
    }

    public void getTimeResponse(String text, TextView mTvDate, TextView mTvTime){
        String[] time = text.split(" ");
        try {
            mTvDate.setText(mTvDate.getText() + time[0]);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {

        }

        try {
            mTvTime.setText(mTvTime.getText() + time[1]);
        } catch (java.lang.ArrayIndexOutOfBoundsException e2) {

        }
    }

    public void setTimeDateResponse(String answer, TextView textView){
        textView.setText(mThisTextNeedToSetTextView);
        if (answer.contains("Not") || answer.contains("command")) {
           // mBluetoothMessage.iAmBusy = false;
            sendStatusMessage();
        }
        mTable = "";
    }

    public void callDialog(){
        mBluetoothMessage.mDialog = DialogHelper.showProgressBar(mActivity, mActivity.getString(R.string.send_request));
    }

    public void sendStatusMessage(){
        //mBluetoothMessage.setMessage(mTable, mActivity, BluetoothCommands.STATUS);

        mBluetoothMessage.mStatus = BluetoothCommands.STATUS;
        mBluetoothMessage.writeMessage(mActivity, BluetoothCommands.STATUS);
        SystemClock.sleep(2000);
        mBluetoothMessage.writeMessage(mActivity, "dd\r\n");
        mTable = "";

    }

    public void sendVersionMessage(){
        mBluetoothMessage.mStatus = BluetoothCommands.VERSION;
        mBluetoothMessage.writeMessage(mActivity, BluetoothCommands.VERSION);
        SystemClock.sleep(2000);
        mBluetoothMessage.writeMessage(mActivity, "dd\r\n");
        mTable = "";
    }
}
