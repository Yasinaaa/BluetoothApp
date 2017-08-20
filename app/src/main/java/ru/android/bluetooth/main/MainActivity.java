package ru.android.bluetooth.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.schedule.helper.ScheduleGenerator;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;
import ru.android.bluetooth.view.CalendarActivity;
import java.util.zip.CRC32;



/**
 * Created by itisioslab on 01.08.17.
 */

public class MainActivity extends AppCompatActivity implements MainModel, BluetoothMessage.BluetoothMessageListener{

    private final String TAG = MainActivity.class.getName();
    @BindView(R.id.tv_device_title)
    TextView mTvDeviceTitle;
    @BindView(R.id.tv_device_address)
    TextView mTvDeviceAddress;
    @BindView(R.id.btn_on_device)
    Button mBtnOnDevice;
    @BindView(R.id.btn_off_device)
    Button mBtnOffDevice;
    @BindView(R.id.cv_on_off_info)
    CardView mCvOnOffInfo;
    @BindView(R.id.tv_day)
    TextView mTvDay;
    @BindView(R.id.tv_on)
    TextView mTvOn;
    @BindView(R.id.tv_off)
    TextView mTvOff;
    @BindView(R.id.cv_controller_functions)
    CardView mCvControllerFunctions;
    @BindView(R.id.tv_reset)
    TextView mTvReset;
    @BindView(R.id.btn_reset_controller)
    Button mBtnResetController;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.ib_sync_status)
    ImageButton mIbSyncStatus;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.ib_sync_version)
    ImageButton mIbSyncVersion;
    @BindView(R.id.ib_change_name)
    ImageButton mIbChangeName;
    @BindView(R.id.tv_change_password)
    TextView mTvChangePassword;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.btn_set_date)
    Button mBtnSetDate;
    @BindView(R.id.cv_schedule)
    CardView mCvSchedule;
    @BindView(R.id.et_schedule)
    EditText mEtSchedule;
    @BindView(R.id.btn_remove_schedule)
    Button mBtnRemoveSchedule;
    @BindView(R.id.tv_edit_schedule)
    TextView mTvEditSchedule;
    @BindView(R.id.tv_generate_schedule)
    TextView mTvGenerateSchedule;
    @BindView(R.id.tv_new_schedule)
    TextView mTvNewSchedule;
    @BindView(R.id.tb_mode_device)
    ToggleButton mTbSwitchModeDevice;
    @BindView(R.id.rl)
    RelativeLayout mRl;

    private RelativeLayout.LayoutParams mRlLayoutParams;
    private BluetoothMessage mBluetoothMessage;
    private String mStatus;

    public double JD = 0;
    public int zone = +4;
    public boolean dst = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);


        init();

        mTbSwitchModeDevice.setChecked(true);
        setModeVisiblity(View.INVISIBLE);
        mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_controller_functions);
        mRlLayoutParams.setMargins(0,10,0,0);
        mCvSchedule.setLayoutParams(mRlLayoutParams);


        testSetData();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void testSetData(){
        //System.out.println(crc.getValue());
        // mBluetoothMessage.writeMessage(BluetoothCommands.RESET);

        //mBluetoothMessage.writeMessage("Set Data\r\n1460\n");
        mBluetoothMessage.writeMessage(0);
        Calendar finishDate = Calendar.getInstance();
        finishDate.add(Calendar.YEAR, 1);
        generateSchedule(Calendar.getInstance(), finishDate, 55.75, 37.50);


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

    private void init(){

        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        //setMessage(BluetoothCommands.STATUS);

        /*setMessage("Set Data\n");
        setMessage(0000002-380000-764-4471);*/

        mRlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

       /* mStatus = BluetoothCommands.SET_DATA;
        mBluetoothMessage.writeMessage(BluetoothCommands.SET_DATA);
        mStatus = "okkk";
        mBluetoothMessage.writeMessage("Ok\r\n");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Bytes=" + editText.getText().toString().getBytes().length);
                mBluetoothMessage.writeMessage(editText.getText().toString());
            }
        });
        String s = "Ok\r\n";
        String t = "Set Data\r\n @11.08.2017 $12:00:00 %0 $12:01:00 %100 1;";
        CRC32 crc = new CRC32();
        crc.update(t.getBytes());
        byte[] bytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(crc.getValue()).array();
        Log.d(TAG,Long.toBinaryString(crc.getValue()));
        mBluetoothMessage.writeMessage(t.getBytes().length + " " + t + " " + Long.toBinaryString(crc.getValue()));*/


       /* try {
            byte[] dd = t.getBytes("UTF-8");
            Log.d("f", "fff");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        /*mStatus = BluetoothCommands.STATUS;
        mBluetoothMessage.writeMessage(BluetoothCommands.STATUS);

        mStatus = BluetoothCommands.GET_TIME;
        mBluetoothMessage.writeMessage(BluetoothCommands.GET_TIME);*/

        mTbSwitchModeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setMode(null);
            }
        });

        mTvEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startActivity(MainActivity.this, CalendarActivity.class);
            }
        });


        setDeviceTitle();
        setGenerationType();
        setDate();
        setClickListeners();
        setChangePassword();
    }


    private void setDeviceTitle(){
        mTvDeviceAddress.setText(BluetoothHelper.getBluetoothUser(getApplicationContext())[0].trim());
        mTvDeviceTitle.setText(BluetoothHelper.getBluetoothUser(getApplicationContext())[1].trim());
    }

  /*  private void setOnOff(String onOf){
        if (onOf.equals("On")){
            setDeviceModeColor(true);
        }else if (onOf.equals("Off")){
            setDeviceModeColor(false);
        }
    }*/

    private void setMode(String mode){

        if (mode == null){
            if(!mTbSwitchModeDevice.getText().equals(getResources().getString(R.string.manual_mode))){
                mStatus = BluetoothCommands.MANUAL_OFF;
                mBluetoothMessage.writeMessage(mStatus);
            }else {
                mStatus = BluetoothCommands.MANUAL_ON;
                mBluetoothMessage.writeMessage(mStatus);
            }

        }else

        if (mode.equals("On")){
            mTbSwitchModeDevice.setChecked(true);
            setModeVisiblity(View.INVISIBLE);
            mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_controller_functions);
        }else if (mode.equals("Off")){
            mTbSwitchModeDevice.setChecked(false);
            setModeVisiblity(View.VISIBLE);
            mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_on_off_info);
        }

        if(mTbSwitchModeDevice.getText().equals(getResources().getString(R.string.manual_mode))){
            setModeVisiblity(View.INVISIBLE);
            mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_controller_functions);
        }else {
            setModeVisiblity(View.VISIBLE);
            mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_on_off_info);
        }
        mRlLayoutParams.setMargins(0,10,0,0);
        mCvSchedule.setLayoutParams(mRlLayoutParams);
    }

    private void setModeVisiblity(int visiblity){
        mCvControllerFunctions.setVisibility(visiblity == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        mCvOnOffInfo.setVisibility(visiblity);

        if(visiblity == View.INVISIBLE){
            setScheduleModeVisiblity(View.GONE);
        }else {
            setScheduleModeVisiblity(View.VISIBLE);
        }

    }

    private void setScheduleModeVisiblity(int visiblity){
        mEtSchedule.setVisibility(visiblity);
        mBtnRemoveSchedule.setVisibility(visiblity);
        mTvEditSchedule.setVisibility(visiblity);
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

    private void setGenerationType(){
        final Activity activity = this;
        mTvGenerateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_choose_generation_schedule_type, null);
                TextView handGeneration = dialogView.findViewById(R.id.tv_generate_schedule_hand);
                handGeneration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                TextView sunRiseSetGeneration = dialogView.findViewById(R.id.tv_generate_schedule_sunrise_set);
                sunRiseSetGeneration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(activity)
                        .setTitle(getString(R.string.input_password))
                        .setView(dialogView)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                passwordDialogBuilder.show();
            }
        });
    }

    private void setDate(){
        mBtnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* DatePickerDialog mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                    }
                },2017, 03, 01);
               // mDatePicker.setTitle("Выберите дату");
                mDatePicker.show();*/
                mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);
            }
        });
    }


    @Override
    public void setDeviceTitle(String title) {
        mTvDeviceTitle.setText(title);
    }

    @Override
    public void setStatus(String status) {
        mTvStatus.setText(status);
    }



    private void setDeviceModeColor(boolean isOn){
        int color1 = getResources().getColor(R.color.accent);
        int color2 = getResources().getColor(R.color.cardview_dark_background);
        if(isOn) {
            mBtnOnDevice.setBackgroundColor(color1);
            mBtnOffDevice.setBackgroundColor(color2);
        }else {
            mBtnOnDevice.setBackgroundColor(color2);
            mBtnOffDevice.setBackgroundColor(color1);
        }
    }

    int count = 0;
    @Override
    public void onResponse(String answer) {
        Log.d(TAG, " " + answer + " count=" + count);


        if(answer.contains("O") ){
            //&& count != 2
            count++;
            mBluetoothMessage.writeMessageD(1);
        }

       /* if (count !=127) {
            count++;
            mBluetoothMessage.writeMessageD(count);
        }*/
       /* try {
            /*if (Integer.parseInt(answer) == 1) {
                mBluetoothMessage.writeMessage(onList);
                mBluetoothMessage.writeMessage(offList);
                //mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);
            }*/
          /*  Integer.parseInt(answer);

            if (count !=127) {
                count++;
                mBluetoothMessage.writeMessageD(count);
            }
        }catch (Exception e){
            /*count = 14;
            mBluetoothMessage.writeMessage(count);*/
       // }

        if(mStatus!= null) {
            switch (mStatus) {
                case BluetoothCommands.DEBUG:
                    Log.d(TAG, answer);
                    break;
                case BluetoothCommands.RESET:
                    mTvReset.setText(answer);
                    break;
                case BluetoothCommands.STATUS:
                    mTvStatus.setText(answer);
                    parseStatus(answer);

                    break;
                case BluetoothCommands.VERSION:
                    mTvVersion.setText(answer.substring(0, answer.indexOf("\n")));
                    break;
                case BluetoothCommands.GET_TIME:
                    mTvDate.setText(answer);
                    break;
                case BluetoothCommands.ON:
                    setDeviceModeColor(true);
                    break;
                case BluetoothCommands.OFF:
                    setDeviceModeColor(false);
                    break;
                case BluetoothCommands.SET_DATA:

                    break;
            }
        }
    }

    private void setClickListeners(){


        mIbSyncStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatus = BluetoothCommands.STATUS;
                mBluetoothMessage.writeMessage(mStatus);
            }
        });
        mBtnOnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(BluetoothCommands.ON);
            }
        });
        mBtnOffDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(BluetoothCommands.OFF);
            }
        });
        mIbSyncVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(BluetoothCommands.VERSION);
            }
        });
        mBtnResetController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(BluetoothCommands.RESET);
            }
        });
    }

    private void setMessage(String status){
        mStatus = status;
        mBluetoothMessage.writeMessage(status);
    }

    private void setMessage(String status, String text){
        mStatus = status;
        mBluetoothMessage.writeMessage(text);
    }

    private void parseStatus(String status){
        String[] parameters = status.replaceAll("\r","").split("\n");
        for(String s: parameters){
            if (s.contains("Manual")){
                setMode(s.split(" ")[1]);
            }
            if (s.contains("Rele")){
                setMode(s.split(" ")[1]);
            }
        }
    }

    private void setChangePassword(){
        mTvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_password, null);

                final EditText mPasswordView = (EditText) dialogView.findViewById(R.id.et_password);


                AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.input_password))
                        .setView(dialogView)

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               // Toast.makeText(getBaseContext(), "Pressed OK", Toast.LENGTH_SHORT).show();
                                setMessage(BluetoothCommands.setPassword(mPasswordView.getText().toString()));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                passwordDialogBuilder.show();
            }
        });
        mIbChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_password, null);


                TextInputLayout textInputLayout = dialogView.findViewById(R.id.til_password);
                textInputLayout.setHint("Новое название устройства");
                final EditText mPasswordView = (EditText) dialogView.findViewById(R.id.et_password);
                mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT);


                AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.input_password))
                        .setView(dialogView)

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(getBaseContext(), "Pressed OK", Toast.LENGTH_SHORT).show();
                                setMessage(BluetoothCommands.setName(mPasswordView.getText().toString()));
                                mTvDeviceTitle.setText(mPasswordView.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                passwordDialogBuilder.show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
