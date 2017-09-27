package ru.android.bluetooth.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.date_time.DateParser;
import ru.android.bluetooth.common.date_time.DateTimeClickListener;
import ru.android.bluetooth.common.date_time.DateTimeView;
import ru.android.bluetooth.utils.CacheHelper;

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
        mDateTimeClickListener.setDateClickListener(dateBtn, mActivity);
        mDateTimeClickListener.setTimeClickListener(timeBtn, mActivity);
    }

    @Override
    public void sendDateMessage(String thisTextNeedToSetTextView, int year, int month, int day) {
        //setMessage(BluetoothCommands.SET_TIME, BluetoothCommands.setTime(i,i1), true);
    }

    @Override
    public void sendTimeMessage(String thisTextNeedToSetTextView, int hour, int min) {
        //setMessage(BluetoothCommands.SET_DATE, BluetoothCommands.setDate(year, month+1, day), true);
    }

    public void setChooseFileDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getString(R.string.generate_dialog_title))
                .setMessage("Выберите Excel файл")
                .setPositiveButton("Загрузить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent()
                                .setType("*/*")
                                .setAction(Intent.ACTION_GET_CONTENT);

                        mActivity.startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialogBuilder.show();
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
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(result);

            mTvDate.setText(mDateParser.setZeros(calendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                            mDateParser.setZeros((calendar.get(Calendar.MONTH) + 1)) + "-" +
                            mDateParser.setZeros(calendar.get(Calendar.YEAR)));

            mTvTime.setText(mDateParser.setZeros(result.getHours()) + ":" +
                    mDateParser.setZeros(result.getMinutes()) + ":" +
                    mDateParser.setZeros(result.getSeconds()));

        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

    }
}
