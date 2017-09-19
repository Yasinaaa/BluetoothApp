package ru.android.bluetooth.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import ru.android.bluetooth.hand_generation.GenerateHandActivity;
import ru.android.bluetooth.main.helper.ResponseView;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.schedule.ScheduleGeneratorActivity;
import ru.android.bluetooth.settings.SettingsActivity;
import ru.android.bluetooth.start.ChooseDeviceActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;
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

    public double JD = 0;
    public int zone = +4;
    public boolean dst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
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
        //setMessage(mTvStatus,BluetoothCommands.STATUS);

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
                setMessage(mTvTime, BluetoothCommands.GET_TIME);
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
                        String i1s = String.valueOf(i1);
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
                               String.valueOf(i), String.valueOf(i1)
                        });
                        setMessage(mTvTime, BluetoothCommands.SET_TIME, BluetoothCommands.setTime(i,i1));
                    }
                },Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
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
                    if(answer.contains("Not") || mTable.contains("command")){
                        answer = answer.replaceAll("[Notcomand ]","");
                        exportToExcel(mTable);
                    }
                    /*if(StringUtils.countMatches(answer, "i") == 2){
                        String[] newArray = answer.split("i");
                    }*/
                    mTable +=answer;



                    break;
                case BluetoothCommands.DEBUG:
                    //TODO!!!
                    //mAutoModePresenter.add(answer);
                    break;
                case BluetoothCommands.RESET:
                    //mTvReset.setText(answer);
                    ResponseView.showSnackbar(mRl,
                            ResponseView.RESET);
                    break;
                case BluetoothCommands.STATUS:
                    mTvStatus.setText(mTvStatus.getText() + answer);
                    parseStatus(answer);
                    ResponseView.showSnackbar(mRl,
                            ResponseView.STATUS);
                    break;
                case BluetoothCommands.VERSION:
                   // mTvVersion.setText(answer.substring(0, answer.indexOf("\n")));
                    mTvVersion.setText(answer);
                    ResponseView.showSnackbar(mRl,
                            ResponseView.VERSION);
                    break;
                case BluetoothCommands.GET_TIME:
                    if(answer.contains(" ")){
                        String[] time = answer.split(" ");
                        mTvDate.setText(time[0]);
                        mTvTime.setText(time[1].substring(0, time[1].indexOf("\r")));
                        ResponseView.showSnackbar(mRl,
                                ResponseView.GET_TIME);
                    }
                    break;
                case BluetoothCommands.ON:
                    setDeviceModeColor(false);
                    ResponseView.showSnackbar(mRl,
                            ResponseView.ON);
                    setMessage(mTvStatus, BluetoothCommands.STATUS);

                    break;
                case BluetoothCommands.OFF:
                    setDeviceModeColor(true);
                    ResponseView.showSnackbar(mRl,
                            ResponseView.OFF);
                    setMessage(mTvStatus, BluetoothCommands.STATUS);
                    break;

                case BluetoothCommands.SET_DATA:

                    break;
                case BluetoothCommands.SET_DATE:
                    if(answer.contains("Ok")){
                        mTvDate.setText(thisTextNeedToSetTextView);
                        ResponseView.showSnackbar(mRl,
                                ResponseView.SET_DATE);
                    }
                    break;
                case BluetoothCommands.SET_TIME:
                    if(answer.contains("Ok")){
                        mTvTime.setText(thisTextNeedToSetTextView);
                        ResponseView.showSnackbar(mRl,
                                ResponseView.SET_TIME);

                    }
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

        mTvEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory(), "schedule.txt");
                Uri uri = Uri.parse("file://" + file.getAbsolutePath());

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });

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
            }/*else if(!s.contains("s") && !s.contains(" ")){
                getTime(s);
            }*/
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
                        .setMessage("Каждый день рассписания должен выглядеть так: \n" +
                                "is=1,134,256")
                        .setPositiveButton("Загрузить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Intent intent = new Intent()
                                        //.setType("*/*")
                                        //.setAction(Intent.ACTION_GET_CONTENT);

                               // startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                                mTable = "";
                                mLoadingTableAlertDialog = ActivityHelper.showProgressBar(activity);
                                setMessage(BluetoothCommands.GET_TABLE);
                                SystemClock.sleep(1000);
                                setMessage(BluetoothCommands.GET_TABLE, "f\r\n");

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
            String title = selectedfile.getLastPathSegment();
            if(title.endsWith(".txt")){
                mEtScheduleName.setText(title.substring(title.indexOf("/") + 1));
                File file = new File(selectedfile.getPath());
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

    private void exportToExcel(String table) {
        ActivityHelper.changeProgressBarText(mLoadingTableAlertDialog, "Запись расписания в файл");
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
            DateParser mDateParser = new DateParser(Calendar.getInstance(), getApplicationContext());

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
                            sheet.addCell(new Label(0, i, mDateParser.getDate(underTextArray[j].
                                    substring(underTextArray[j].indexOf("=") + 1,
                                    underTextArray[j].indexOf(",")))));

                        }catch (java.lang.StringIndexOutOfBoundsException e){

                        }

                        try {

                        sheet.addCell(new Label(1, i, mDateParser.getTime(underTextArray[j].substring(
                                underTextArray[j].lastIndexOf(",") + 1,
                                underTextArray[j].length()))));
                        }catch (java.lang.StringIndexOutOfBoundsException e){

                        }

                        try{
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
            ActivityHelper.hideProgressBar(mLoadingTableAlertDialog);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
                    .setTitle("Файл сохранен")
                    .setMessage("Сохранен в " + file.getPath())
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            dialogBuilder.show();
            mEtScheduleName.setText(file.getPath());
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
