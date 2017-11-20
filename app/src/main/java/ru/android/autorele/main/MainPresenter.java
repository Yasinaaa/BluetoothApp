package ru.android.autorele.main;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
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

import ru.android.autorele.R;
import ru.android.autorele.bluetooth.BluetoothCommands;
import ru.android.autorele.bluetooth.BluetoothMessage;
import ru.android.autorele.common.date_time.DateParser;
import ru.android.autorele.common.date_time.DateTimeClickListener;
import ru.android.autorele.common.date_time.DateTimeView;
import ru.android.autorele.utils.CacheHelper;
import ru.android.autorele.utils.DialogHelper;


/**
 * Created by yasina on 22.08.17.
 */

public class MainPresenter implements DateTimeView.TimeAndDate{

    private final String TAG = "MainPresenter";
    private Activity mActivity;
    private Context mContext;
    private BluetoothMessage mBluetoothMessage;
    private DateParser mDateParser;
    private File mFile;
    private String mTable = "";
    private String mThisTextNeedToSetTextView;

    public MainPresenter(Activity activity, BluetoothMessage bluetoothMessage) {
        this.mActivity = activity;
        this.mContext = mActivity.getApplicationContext();
        this.mBluetoothMessage = bluetoothMessage;
        mDateParser = new DateParser();
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

        final DateTimeView.TimeAndDate timeAndDate = this;

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog();
                DateTimeClickListener.setDateClickListener(mActivity, timeAndDate, mDateParser);
            }
        });

        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog();
                DateTimeClickListener.setTimeClickListener(mActivity, timeAndDate, mDateParser);
            }
        });

    }

    @Override
    public void sendDateMessage(String thisTextNeedToSetTextView, int year, int month, int day) {
        this.mThisTextNeedToSetTextView = thisTextNeedToSetTextView;
        setMessage(mTable, mActivity, BluetoothCommands.SET_DATE, BluetoothCommands.setDate(year, month+1, day), true);
    }

    @Override
    public void sendTimeMessage(String thisTextNeedToSetTextView, int hour, int min) {
        this.mThisTextNeedToSetTextView = thisTextNeedToSetTextView;
       setMessage(mTable, mActivity,
                BluetoothCommands.SET_TIME, BluetoothCommands.setTime(hour, min), true);
    }

    public void setMessage(String responseText, Activity activity, String status, String text, boolean noDialog){
        responseText = "";
        mBluetoothMessage.mStatus = status;
        if (!noDialog){
            mBluetoothMessage.mDialog = DialogHelper.showProgressBar(activity, activity.getString(R.string.send_request));
        }
        mBluetoothMessage.writeMessageWithNoCommand(activity, text);
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
    public void parseResponse2(String answer, ResponseParseView responseView){
        DialogHelper.hideProgressBar(mBluetoothMessage.mDialog);
        mTable += answer;
        responseView.nextJob(mTable);
    }

    public void getTimeResponse(String text, TextView mTvDate, TextView mTvTime){
        String[] time = text.split(" ");
        try {
            mTvDate.setText(mTvDate.getText() + time[0]);
        } catch (ArrayIndexOutOfBoundsException e) {

        }

        try {
            mTvTime.setText(mTvTime.getText() + time[1]);
        } catch (ArrayIndexOutOfBoundsException e2) {

        }
    }

    public void setTimeDateResponse(String answer, TextView textView){
        textView.setText(mThisTextNeedToSetTextView);
        if (answer.contains("Not") || answer.contains("command")) {
            sendStatusMessage();
        }
        mTable = "";
    }

    public void callDialog(){
        mBluetoothMessage.mDialog = DialogHelper.showProgressBar(mActivity, mActivity.getString(R.string.send_request));
    }

    public void sendStatusMessage(){
        mBluetoothMessage.mStatus = BluetoothCommands.STATUS;
        mBluetoothMessage.writeMessage(mActivity, BluetoothCommands.STATUS);
        SystemClock.sleep(2000);
        mBluetoothMessage.writeMessage(mActivity, BluetoothCommands.NOT_COMMAND);
        mTable = "";
    }

    public void sendVersionMessage(){
        mBluetoothMessage.mStatus = BluetoothCommands.VERSION;
        mBluetoothMessage.writeMessage(mActivity, BluetoothCommands.VERSION);
        mTable = "";
    }
}
