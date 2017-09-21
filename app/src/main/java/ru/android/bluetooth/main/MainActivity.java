package ru.android.bluetooth.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
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
import ru.android.bluetooth.common.DateParser;
import ru.android.bluetooth.hand_generation.GenerateHandActivity;
import ru.android.bluetooth.main.helper.ScheduleLoading;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.schedule.ScheduleGeneratorActivity;
import ru.android.bluetooth.settings.SettingsActivity;
import ru.android.bluetooth.start.ChooseDeviceActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.CacheHelper;
import ru.android.bluetooth.view.CalendarActivity;


/**
 * Created by itisioslab on 01.08.17.
 */

public class MainActivity extends RootActivity implements MainModule.ManualModeView, MainModule.AutoModeView,
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
    @BindView(R.id.btn_sync_date)
    Button mIbSyncDate;
    @BindView(R.id.et_schedule_name)
    EditText mEtScheduleName;
    @BindView(R.id.btn_generate_schedule)
    Button mBtnGenerateSchedule;
    @BindView(R.id.btn_edit_schedule)
    Button mBtnEditSchedule;
    @BindView(R.id.btn_load_schedule)
    Button mBtnLoadSchedule;

    private String thisTextNeedToSetTextView;
    private RelativeLayout.LayoutParams mRlLayoutParams;
    private BluetoothMessage mBluetoothMessage;
    private String mStatus;
    private AutoModePresenter mAutoModePresenter;
    private DateParser mDateParser;
    private AlertDialog mLoadingTableAlertDialog;
    private ScheduleLoading scheduleLoading;
    private File file;
    private static final int REQUEST_READ_PERMISSION = 785;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private String mTable;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
    }

    @Override
    public void requestReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scheduleLoading.parceSchedule(file);
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
        mDateParser = new DateParser();
        setScheduleFilePath();

        ActivityHelper.setVisibleLogoIcon(this);

        mRlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_controller_functions);
        mRlLayoutParams.setMargins(0,10,0,0);
        mCvSchedule.setLayoutParams(mRlLayoutParams);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        setMessage(BluetoothCommands.STATUS);

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

    private void setDateClickListeners(){
        final Calendar calendar = Calendar.getInstance();
        mIbSyncDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvTime.setText("");
                mTvDate.setText("");
                setMessage(BluetoothCommands.GET_TIME);
            }
        });
        mBtnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();

                        String i1s = String.valueOf(i1+1);
                        if(i1s.length() == 1){
                            i1s = "0" + i1s;
                        }
                        String i2s = String.valueOf(i2);
                        if(i2s.length() == 1){
                            i2s = "0" + (i2 + 1);
                        }
                        thisTextNeedToSetTextView = String.format("%s-%s-%s", new String[]{
                                i2s+"", i1s, i+""
                        });
                        setMessage(BluetoothCommands.SET_DATE, BluetoothCommands.setDate(year, month+1, day), true);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.show();
                //testSetData();
            }
        });

        mBtnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        thisTextNeedToSetTextView = String.format("%s:%s", new String[]{
                               setZeros(i), setZeros(i1)
                        });
                        setMessage(BluetoothCommands.SET_TIME, BluetoothCommands.setTime(i,i1), true);
                    }
                },Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }

    private String setZeros(int value){
        String i1s = String.valueOf(value);
        if(i1s.length() == 1){
            i1s = "0" + i1s;
        }
        return i1s;
    }


    @Override
    public void dataCreated(int[] onList, int[] offList) {
        mStatus = BluetoothCommands.GET_TABLE;
        mBluetoothMessage.writeMessage(onList, offList);
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
        Log.d(TAG, mStatus + " " + answer);

        if(mStatus!= null) {
            switch (mStatus) {

                case BluetoothCommands.STATUS:
                    if (answer.contains("Not") || answer.contains("command")) {
                        ActivityHelper.hideProgressBar(mLoadingTableAlertDialog);
                        answer = answer.replaceAll("[Notcomand ]", "");
                        mTable += answer;
                        //mTvStatus.setText(mTvStatus.getText() + answer);
                        mTvStatus.setText(mTable);
                        parseStatus(mTable);
                    }else if(answer.equals("")){
                        ActivityHelper.hideProgressBar(mLoadingTableAlertDialog);
                    }else {
                        mTable += answer;
                    }
                    break;

                case BluetoothCommands.VERSION:
                    if (answer.contains("Not") || answer.contains("command")) {
                        ActivityHelper.hideProgressBar(mLoadingTableAlertDialog);
                        answer = answer.replaceAll("[Notcomand ]", "");
                        mTable += answer;
                        mTvVersion.setText(mTable);
                    }else {
                        mTable += answer;
                    }

                    break;

                case BluetoothCommands.GET_TIME:
                    if (answer.contains("Not") || answer.contains("command")) {
                        ActivityHelper.hideProgressBar(mLoadingTableAlertDialog);
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
                    mTvDate.setText(thisTextNeedToSetTextView);
                    if (answer.contains("Not") || answer.contains("command")) {
                        setMessage(BluetoothCommands.STATUS, true);
                    }
                    break;

                case BluetoothCommands.SET_TIME:
                    mTvTime.setText(thisTextNeedToSetTextView);
                    if (answer.contains("Not") || answer.contains("command")) {
                        setMessage(BluetoothCommands.STATUS, true);
                    }

                    break;
            }

        }
    }

    @Override
    public void setClickListeners(){

        setScheduleButtons();
        setDateClickListeners();

        mIbSyncStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(BluetoothCommands.STATUS);
            }
        });

        mIbSyncVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(BluetoothCommands.VERSION);
            }
        });

    }

    private void setMessage(String status, boolean noDialog){
        mStatus = status;
        if (!noDialog){
            mLoadingTableAlertDialog = ActivityHelper.showProgressBar(mActivity, "Отправка запроса");
        }
        setMessageOnly(status);
    }

    private void setMessage(String status){
        mStatus = status;
        mLoadingTableAlertDialog = ActivityHelper.showProgressBar(mActivity, "Отправка запроса");
        setMessageOnly(status);
    }


    private void setMessage(String status, String text){
        mStatus = status;
        mLoadingTableAlertDialog = ActivityHelper.showProgressBar(mActivity, "Отправка запроса");
        setMessageOnly(text);
    }

    private void setMessage(String status, String text, boolean noDialog){
        mStatus = status;
        if (!noDialog){
            mLoadingTableAlertDialog = ActivityHelper.showProgressBar(mActivity, "Отправка запроса");
        }
        setMessageOnly(text);
    }

    private void setMessageOnly(String text){

        mTable = "";
        mBluetoothMessage.writeMessage(mActivity,text);
        SystemClock.sleep(2000);
        mBluetoothMessage.writeMessage(mActivity,"dd\r\n");
    }

    private void parseStatus(String status){
        String[] parameters = status.replaceAll("\r","").split("\n");
        for(String s: parameters){
            if(!s.matches(".*[a-zA-Z]+.*")){
                getTime(s);
            }
        }
    }

    private void getTime(String data){
        String[] allData = data.split(" ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm:ss", Locale.ENGLISH);
        try {
            Date result = dateFormat.parse(data);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(result);

            mTvDate.setText(
                    mDateParser.setZeros(calendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                    mDateParser.setZeros((calendar.get(Calendar.MONTH) + 1)) + "-" +
                    mDateParser.setZeros(calendar.get(Calendar.YEAR)));

            mTvTime.setText(mDateParser.setZeros(result.getHours()) + ":" +
                    mDateParser.setZeros(result.getMinutes()) + ":" +
                    mDateParser.setZeros(result.getSeconds()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_settings);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ActivityHelper.startActivity(mActivity, SettingsActivity.class);
                /*Intent intent = new Intent(mActivity,SettingsActivity.class);
                startActivity(intent);*/
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void addItemToDateRecyclerView() {

    }


    private void setScheduleButtons(){
        mBtnEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startActivity(MainActivity.this, CalendarActivity.class);
            }
        });

        mBtnGenerateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = mActivity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_choose_generation_schedule_type, null);
                TextView handGeneration = dialogView.findViewById(R.id.tv_generate_schedule_hand);
                handGeneration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityHelper.startActivity(MainActivity.this, GenerateHandActivity.class);

                    }
                });
                TextView sunRiseSetGeneration = dialogView.findViewById(R.id.tv_generate_schedule_sunrise_set);
                sunRiseSetGeneration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityHelper.startActivity(MainActivity.this, ScheduleGeneratorActivity.class);
                    }
                });

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                        .setTitle(getString(R.string.generate_dialog_title))
                        .setView(dialogView)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                dialogBuilder.show();
            }
        });


        mBtnLoadSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                        .setTitle(getString(R.string.generate_dialog_title))
                        .setMessage("Выберите Excel файл")
                        .setPositiveButton("Загрузить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent()
                                        .setType("*/*")
                                        .setAction(Intent.ACTION_GET_CONTENT);

                                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                dialogBuilder.show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode==RESULT_OK) {

            Uri selectedfile = data.getData();
            String r = Environment.getExternalStorageDirectory().getPath();
            String title = selectedfile.getLastPathSegment().substring(selectedfile.getLastPathSegment().indexOf(":")+1);
            if(title.endsWith(".xls")){

                File file = new File(r+"/"+title);
                if (file.exists()){
                    Log.d("t", "exists");
                }
                mEtScheduleName.setText(file.getPath());
                CacheHelper.setSchedulePath(getApplicationContext(), file.getPath());

                ScheduleLoading scheduleLoading = new ScheduleLoading(this, mBluetoothMessage, this);
                scheduleLoading.parceSchedule(file);

            }else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                        .setTitle(getString(R.string.generate_dialog_title))
                        .setMessage("Не правильный формат файла")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                dialogBuilder.show();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setScheduleFilePath();
    }

    private void setScheduleFilePath(){
        String scheduleFile = CacheHelper.getSchedulePath(getApplicationContext());
        if(!scheduleFile.equals("")){
            mEtScheduleName.setVisibility(View.VISIBLE);
            mEtScheduleName.setText(scheduleFile);
        }else {
            mEtScheduleName.setVisibility(View.GONE);
        }
    }



}
