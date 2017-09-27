package ru.android.bluetooth.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.date_time.DateParser;
import ru.android.bluetooth.main.helper.ScheduleLoading;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.settings.SettingsActivity;
import ru.android.bluetooth.start.ChooseDeviceActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.CacheHelper;
import ru.android.bluetooth.calendar.CalendarActivity;
import ru.android.bluetooth.utils.DialogHelper;


/**
 * Created by itisioslab on 01.08.17.
 */

public class MainActivity extends RootActivity implements MainModule.View,
        BluetoothMessage.BluetoothMessageListener{

    @BindView(R.id.cv_controller_functions)
    CardView mCvControllerFunctions;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.btn_sync_status)
    Button mIbSyncStatus;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.btn_sync_version)
    Button mIbSyncVersion;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.btn_set_date)
    Button mBtnSetDate;
    @BindView(R.id.cv_schedule)
    CardView mCvSchedule;
    @BindView(R.id.tv_edit_schedule)
    TextView mTvEditSchedule;
    @BindView(R.id.tv_generate_schedule)
    TextView mTvGenerateSchedule;
    @BindView(R.id.tv_new_schedule)
    TextView mTvNewSchedule;
    @BindView(R.id.rl)
    RelativeLayout mRl;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.btn_set_time)
    Button mBtnSetTime;
    @BindView(R.id.et_schedule_name)
    EditText mEtScheduleName;
    @BindView(R.id.btn_edit_schedule)
    Button mBtnEditSchedule;
    @BindView(R.id.btn_load_schedule)
    Button mBtnLoadSchedule;

    private String mThisTextNeedToSetTextView;
    private BluetoothMessage mBluetoothMessage;

    private MainPresenter mMainPresenter;
    private ScheduleLoading scheduleLoading;
    private File mFile;
    private String mTable = "";
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ActivityHelper.REQUEST_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scheduleLoading.parceSchedule(mFile);
        }
    }

    @Override
    public void init(){
        // close previous Dialog
        try {
            ChooseDeviceActivity.dialog.cancel();
        }catch (java.lang.NullPointerException e){

        }

        mActivity = this;

        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        mBluetoothMessage.setMessage(mTable, mActivity, BluetoothCommands.STATUS);

        mMainPresenter = new MainPresenter(mActivity, mBluetoothMessage);
        mMainPresenter.setScheduleFilePath(mEtScheduleName);

        scheduleLoading = new ScheduleLoading(this, mActivity);

    }

    // arguing for it
    private int setOppositeVisiblity(int visiblity){
        return visiblity == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void setTag() {
        TAG = "MainActivity";
    }

    @Override
    public void dataCreated(int[] onList, int[] offList) {
        mBluetoothMessage.mStatus = BluetoothCommands.GET_TABLE;
        mBluetoothMessage.writeMessage(mActivity, onList, offList);
    }

    @Override
    public void setDeviceTitle(String title) {
        //mTvDeviceTitle.setText(title);
    }

    @Override
    public void setStatus(String status) {
        mTvStatus.setText(status);
    }

    @Override
    public void onResponse(String answer) {
        Log.d(TAG, mBluetoothMessage.mStatus + " " + answer);

        if(mBluetoothMessage.mStatus!= null) {
            switch (mBluetoothMessage.mStatus) {

                case BluetoothCommands.STATUS:
                    if (answer.contains("Not") || answer.contains("command")) {
                        DialogHelper.hideProgressBar(mBluetoothMessage.mDialog);
                        answer = answer.replaceAll("[Notcomand ]", "");
                        mTable += answer;
                        mTvStatus.setText(mTable);
                        mMainPresenter.parseStatus(mTable, mTvDate, mTvTime);
                    }else if(answer.equals("")){
                        DialogHelper.hideProgressBar(mBluetoothMessage.mDialog);
                    }else {
                        mTable += answer;
                    }
                    break;

                case BluetoothCommands.VERSION:
                    if (answer.contains("Not") || answer.contains("command")) {
                        DialogHelper.hideProgressBar(mBluetoothMessage.mDialog);
                        answer = answer.replaceAll("[Notcomand ]", "");
                        mTable += answer;
                        mTvVersion.setText(mTable);
                    }else {
                        mTable += answer;
                    }

                    break;

                case BluetoothCommands.GET_TIME:
                    if (answer.contains("Not") || answer.contains("command")) {
                        DialogHelper.hideProgressBar(mBluetoothMessage.mDialog);
                        answer = answer.replaceAll("[Notcomand ]", "");
                        mTable += answer;
                        String[] time = mTable.split(" ");
                        try {
                            mTvDate.setText(mTvDate.getText() + time[0]);
                        } catch (java.lang.ArrayIndexOutOfBoundsException e) {

                        }

                        try {
                            mTvTime.setText(mTvTime.getText() + time[1]);
                        } catch (java.lang.ArrayIndexOutOfBoundsException e2) {

                        }
                    }else {
                        mTable += answer;
                    }
                    break;

                case BluetoothCommands.SET_DATE:
                    mTvDate.setText(mThisTextNeedToSetTextView);
                    if (answer.contains("Not") || answer.contains("command")) {
                        mBluetoothMessage.setMessage(mTable, mActivity, BluetoothCommands.STATUS, true);
                    }
                    break;

                case BluetoothCommands.SET_TIME:
                    mTvTime.setText(mThisTextNeedToSetTextView);
                    if (answer.contains("Not") || answer.contains("command")) {
                        mBluetoothMessage.setMessage(mTable, mActivity, BluetoothCommands.STATUS, true);
                    }
                    break;
            }
        }
    }

    @Override
    public void setClickListeners(){

        setScheduleButtons();
        mMainPresenter.setDateTimeClickListeners(mBtnSetTime, mBtnSetDate);

        mIbSyncStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothMessage.setMessage(mTable, mActivity, BluetoothCommands.STATUS);
            }
        });

        mIbSyncVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothMessage.setMessage(mTable, mActivity, BluetoothCommands.VERSION);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_settings);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ActivityHelper.startActivity(mActivity, SettingsActivity.class);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void setScheduleButtons(){
        mBtnEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startActivity(MainActivity.this, CalendarActivity.class);
            }
        });

        mBtnLoadSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPresenter.setChooseFileDialog();
            }
        });
    }

    @Override
    public void setScheduleTitle(String title){
        mEtScheduleName.setText(title);
        CacheHelper.setSchedulePath(getApplicationContext(), title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode==RESULT_OK) {

            final Uri selectedfile = data.getData();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                    .setTitle("Uri")
                    .setMessage(selectedfile.getPath())
                    .setNegativeButton("Продолжить", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String r = Environment.getExternalStorageDirectory().getPath();
                            String title = selectedfile.getLastPathSegment().substring(selectedfile.getLastPathSegment().indexOf(":")+1);
                            if(title.endsWith(".xls")){

                                mFile = new File(r+"/"+title);
                                if (mFile.exists()){
                                    Log.d(TAG, "exists");
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        scheduleLoading.parceSchedule(mFile);
                                    }
                                });

                            }
                        }
                    });
            dialogBuilder.show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.setScheduleFilePath(mEtScheduleName);
    }





}
