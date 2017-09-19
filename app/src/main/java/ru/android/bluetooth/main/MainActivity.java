package ru.android.bluetooth.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.DateParser;
import ru.android.bluetooth.hand_generation.GenerateHandActivity;
import ru.android.bluetooth.main.helper.ResponseView;
import ru.android.bluetooth.main.helper.ScheduleLoading;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.schedule.ScheduleGeneratorActivity;
import ru.android.bluetooth.settings.SettingsActivity;
import ru.android.bluetooth.start.ChooseDeviceActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;
import ru.android.bluetooth.utils.CacheHelper;
import ru.android.bluetooth.view.CalendarActivity;

import static java.security.AccessController.getContext;


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
    /*@BindView(R.id.btn_mode_auto)
    Button mBtnAutoMode;
    @BindView(R.id.btn_mode_manual)
    Button mBtnManualMode;*/
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
        /*PackageManager pm = getApplicationContext().getPackageManager();
        int hasPerm = pm.checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getApplicationContext().getPackageName());

        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
*/
    }

    private static final int REQUEST_READ_PERMISSION = 785;
    private static final int REQUEST_WRITE_PERMISSION = 786;

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
            //openFilePicker();
        }
    }
   /* private void testSetData(){

        Calendar finishDate = Calendar.getInstance();
        finishDate.add(Calendar.YEAR, 1);
        generateSchedule(Calendar.getInstance(), finishDate, 55.75, 37.50);
        mBluetoothMessage.writeMessage(onList, offList);

       // mBluetoothMessage.writeMessage();
    }

    private int[] onList = new int[365];
    private int[] offList = new int[365];

    private void generateSchedule(Calendar startDate, Calendar endDate, double latitude, double longitude){
        Calendar currentDate = Calendar.getInstance();
        double sunRise;
        double sunSet;
        int d = 0;
        while(startDate.compareTo(endDate) <= 0 && d != 365){

            JD = ScheduleGenerator.calcJD(startDate);
            sunRise = ScheduleGenerator.calcSunRiseUTC(JD, latitude, longitude);
            sunSet = ScheduleGenerator.calcSunSetUTC(JD, latitude, longitude);

            onList[d] = Integer.parseInt(ScheduleGenerator.getTimeString(true,sunRise, zone, JD, dst));
            offList[d] = Integer.parseInt(ScheduleGenerator.getTimeString(true,sunSet, zone, JD, dst));
            d++;
            startDate.add(Calendar.DAY_OF_YEAR, 1);

        }
    }
*/

    @Override
    public void init(){
        // close previous Dialog
        try {
            ChooseDeviceActivity.dialog.cancel();
        }catch (java.lang.NullPointerException e){

        }

        setScheduleFilePath();

        ActivityHelper.setVisibleLogoIcon(this);
        //mTbSwitchModeDevice.setChecked(true);

        //setModeVisiblity(View.INVISIBLE);
        mRlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_controller_functions);
        mRlLayoutParams.setMargins(0,10,0,0);
        mCvSchedule.setLayoutParams(mRlLayoutParams);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        setMessage(mTvStatus,BluetoothCommands.STATUS);

    }

    private void setOnOff(String onOf){
        if (onOf.equals("On")){
            setDeviceModeColor(false);
        }else if (onOf.equals("Off")){
            setDeviceModeColor(true);
        }
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

    private void setDateCliskListeners(){
        final Calendar calendar = Calendar.getInstance();
        mIbSyncDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvTime.setText("");
                mTvDate.setText("");
                setMessage(BluetoothCommands.GET_TIME);
               // testSetData();
               // setMessage(BluetoothCommands.GET_TABLE);
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
                        setMessage(mTvDate, BluetoothCommands.SET_DATE, BluetoothCommands.setDate(year, month+1, day));

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
                        setMessage(mTvTime, BluetoothCommands.SET_TIME, BluetoothCommands.setTime(i,i1));
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

    private void setDeviceModeColor(boolean isOn){
        Drawable color1 = getResources().getDrawable(R.drawable.btn_off);
        Drawable color2 = getResources().getDrawable(R.drawable.btn_on);
       /* if(isOn) {
            mBtnOnDevice.setBackground(color1);
            mBtnOffDevice.setBackground(color2);
        }else {
            mBtnOnDevice.setBackground(color2);
            mBtnOffDevice.setBackground(color1);
        }*/
    }

    int count = 0;
    //String mTable = "";
    AlertDialog mLoadingTableAlertDialog;
    @Override
    public void onResponse(String answer) {
        Log.d(TAG, " " + answer);
       /* if(count !=2){
            mBluetoothMessage.writeMessage();
            count++;
        }*/

        if(mStatus!= null) {
            //Log.d(TAG, "mStatus=" + mStatus + " " + answer);
            switch (mStatus) {
                case BluetoothCommands.GET_TABLE:

                    break;
                case BluetoothCommands.DEBUG:
                    //TODO!!!
                    //mAutoModePresenter.add(answer);
                    break;
                case BluetoothCommands.RESET:
                    //mTvReset.setText(answer);
                    /*ResponseView.showSnackbar(mRl,
                            ResponseView.RESET);*/
                    break;
                case BluetoothCommands.STATUS:
                    mTvStatus.setText(mTvStatus.getText() + answer);
                    parseStatus(answer);
                    /*ResponseView.showSnackbar(mRl,
                            ResponseView.STATUS);*/
                    break;
                case BluetoothCommands.VERSION:
                   // mTvVersion.setText(answer.substring(0, answer.indexOf("\n")));
                    mTvVersion.setText(mTvVersion.getText() + answer);
                    /*ResponseView.showSnackbar(mRl,
                            ResponseView.VERSION);*/
                    break;
                case BluetoothCommands.GET_TIME:
                    String[] time = answer.split(" ");
                    try{
                        mTvDate.setText(mTvDate.getText() + time[0]);
                    }catch (java.lang.ArrayIndexOutOfBoundsException e){

                    }

                    try{
                        mTvTime.setText(mTvTime.getText() + time[1]);
                    }catch (java.lang.ArrayIndexOutOfBoundsException e2){

                    }

                    //mTvTime.setText(time[1].substring(0, time[1].indexOf("\r")));


                    break;
                case BluetoothCommands.ON:
                    setDeviceModeColor(false);
                    /*ResponseView.showSnackbar(mRl,
                            ResponseView.ON);*/
                    setMessage(mTvStatus, BluetoothCommands.STATUS);

                    break;
                case BluetoothCommands.OFF:
                    setDeviceModeColor(true);
                    /*ResponseView.showSnackbar(mRl,
                            ResponseView.OFF);*/
                    setMessage(mTvStatus, BluetoothCommands.STATUS);
                    break;

                case BluetoothCommands.SET_DATA:

                    break;
                case BluetoothCommands.SET_DATE:
                    mTvDate.setText(thisTextNeedToSetTextView);
                    setMessage(mTvStatus, BluetoothCommands.STATUS);
                   /* if(answer.contains("Ok")){

                        ResponseView.showSnackbar(mRl,
                                ResponseView.SET_DATE);
                    }*/
                    break;
                case BluetoothCommands.SET_TIME:
                    mTvTime.setText(thisTextNeedToSetTextView);
                    setMessage(mTvStatus, BluetoothCommands.STATUS);
                    /*if(answer.contains("Ok")){

                        ResponseView.showSnackbar(mRl,
                                ResponseView.SET_TIME);

                    }*/
                    break;
                case BluetoothCommands.MANUAL_ON:
                    if(answer.contains("Ok")){
                        //ResponseView.showSnackbar(getWindow().getDecorView().getRootView(),
                        ResponseView.showSnackbar(mRl,
                                ResponseView.MANUAL_ON);
                        setMessage(mTvStatus, BluetoothCommands.STATUS);
                    }
                    break;

                case BluetoothCommands.MANUAL_OFF:
                    if(answer.contains("Ok")){
                        ResponseView.showSnackbar(mRl,
                                ResponseView.MANUAL_OFF);
                        //setMessage(BluetoothCommands.DEBUG);
                       // mAutoModePresenter.createDatesView(mRvOnOffInfo);
                        setMessage(mTvStatus, BluetoothCommands.STATUS);
                    }
                    break;
            }

        }
    }
    @Override

    public void setClickListeners(){

        setScheduleButtons();
        setDateCliskListeners();

        /*mTvEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory(), "schedule.txt");
                Uri uri = Uri.parse("file://" + file.getAbsolutePath());

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });*/

       /* mBtnManualMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(!mTbSwitchModeDevice.getText().equals(getResources().getString(R.string.manual_mode))){
                    setMessage(BluetoothCommands.MANUAL_OFF);

                    mAutoModePresenter = new AutoModePresenter(getApplicationContext(), mBluetoothMessage);
                    mAutoModePresenter.createDatesView(mRvOnOffInfo);

                    if(mAutoModePresenter.isHaveScheduleTxt()){
                        setModeVisiblity(View.VISIBLE);
                    }else {
                        setModeVisiblity(View.INVISIBLE);
                    }

                }else {
                    setModeVisiblity(View.INVISIBLE);
                    setMessage(BluetoothCommands.MANUAL_ON);
                }*/
                //setModeVisiblity(View.INVISIBLE);
         /*       setMessage(BluetoothCommands.MANUAL_ON);
            }
        });

        mBtnAutoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(BluetoothCommands.MANUAL_OFF);

                mAutoModePresenter = new AutoModePresenter(getApplicationContext(), mBluetoothMessage);
                mAutoModePresenter.createDatesView(mRvOnOffInfo);
                //setModeVisiblity(View.VISIBLE);

            }
        });*/

        mIbSyncStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(mTvStatus, BluetoothCommands.STATUS);
            }
        });

        mIbSyncVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(mTvVersion, BluetoothCommands.VERSION);
               // testSetData();
            }
        });

    }

    private void setMessage(String status){
        mStatus = status;
        mBluetoothMessage.writeMessage(status);
    }

    private void setMessage(TextView textView, String status){
        textView.setText("");
        mStatus = status;
        mBluetoothMessage.writeMessage(status);
    }

    private void setMessage(String status, String text){
        mStatus = status;
        mBluetoothMessage.writeMessage(text);
    }

    private void setMessage(TextView textView, String status, String text){
        textView.setText("");
        mStatus = status;
        mBluetoothMessage.writeMessage(text);
    }

    private void parseStatus(String status){
        String[] parameters = status.replaceAll("\r","").split("\n");
        for(String s: parameters){

            if (s.contains("Manual")){
               // setMode(s.split(" ")[1]);
            }else
            if (s.contains("Rele")){
                setOnOff(s.split(" ")[1]);
            }else
                if(!s.matches("[a-zA-Z]*")){
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
            mTvDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                    (calendar.get(Calendar.MONTH) + 1)+ "-" + calendar.get(Calendar.YEAR));
            mTvTime.setText(result.getHours() + ":" + result.getMinutes() + ":" + result.getMinutes());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        activity = this;
        final MenuItem searchItem = menu.findItem(R.id.action_settings);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //ActivityHelper.startActivity(activity, SettingsActivity.class);
                Intent intent = new Intent(activity,SettingsActivity.class);
                startActivity(intent);
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

    String mTable;
    Activity activity = this;
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
                LayoutInflater inflater = activity.getLayoutInflater();
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

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
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
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
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
                //mEtScheduleName.setText(file.getPath());
                //CacheHelper.setSchedulePath(getApplicationContext(), file.getPath());

                ScheduleLoading scheduleLoading = new ScheduleLoading(this, mBluetoothMessage, this);
                scheduleLoading.parceSchedule(file);

            }else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
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
