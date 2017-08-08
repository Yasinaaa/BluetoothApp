package ru.android.bluetooth.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;
import ru.android.bluetooth.view.CalendarActivity;


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void init(){

        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);

        mRlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mStatus = BluetoothCommands.STATUS;
        mBluetoothMessage.writeMessage(BluetoothCommands.STATUS);

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
    }

    private void setDeviceTitle(){
        mTvDeviceAddress.setText(BluetoothHelper.getBluetoothUser(getApplicationContext())[0]);
        mTvDeviceTitle.setText(BluetoothHelper.getBluetoothUser(getApplicationContext())[1]);
    }

    private void setOnOff(String onOf){
        if (onOf.equals("On")){
            setDeviceModeColor(true);
        }else if (onOf.equals("Off")){
            setDeviceModeColor(false);
        }
    }

    private void setMode(String mode){

        if (mode == null){
            if(mTbSwitchModeDevice.isChecked()){
                mStatus = BluetoothCommands.MANUAL_OFF;
                mBluetoothMessage.writeMessage(mStatus);
            }else {
                mStatus = BluetoothCommands.MANUAL_ON;
                mBluetoothMessage.writeMessage(mStatus);
            }

        }else

        if (mode.equals("On")){
            mTbSwitchModeDevice.setChecked(true);
        }else if (mode.equals("Off")){
            mTbSwitchModeDevice.setChecked(false);
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
                DatePickerDialog mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                    }
                },2017, 03, 01);
                mDatePicker.setTitle("Выберите дату");
                mDatePicker.show();
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

    @Override
    public void onResponse(String answer) {
        Log.d(TAG, answer);
        switch (mStatus){
            case BluetoothCommands.DEBUG:
                Log.d(TAG, answer);
                break;
            case BluetoothCommands.RESET:
                mTvReset.setText(answer);
                break;
            case BluetoothCommands.STATUS:
                mTvStatus.setText(answer);
                break;
            case BluetoothCommands.VERSION:
                mTvVersion.setText(answer);
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

    private void setClickListeners(){
        mIbSyncStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatus = BluetoothCommands.STATUS;
                mBluetoothMessage.writeMessage(mStatus);
            }
        });
    }

    private void parseStatus(String status){
        String[] parameters = status.split("\n");
        for(String s: parameters){
            if (s.contains("Manual")){
                setMode(s.split(" ")[1]);
            }
            if (s.contains("Rele1")){
                setMode(s.split(" ")[1]);
            }
        }
    }
}
