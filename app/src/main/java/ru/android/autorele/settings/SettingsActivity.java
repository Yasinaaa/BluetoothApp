package ru.android.autorele.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import ru.android.autorele.R;
import ru.android.autorele.bluetooth.BluetoothCommands;
import ru.android.autorele.bluetooth.BluetoothMessage;
import ru.android.autorele.bluetooth.BluetoothModule;
import ru.android.autorele.common.location.LocationActivity;
import ru.android.autorele.main.helper.ResponseView;
import ru.android.autorele.start.ChooseDeviceView;
import ru.android.autorele.utils.BluetoothHelper;
import ru.android.autorele.utils.CacheHelper;
import ru.android.autorele.utils.DialogHelper;

/**
 * Created by yasina on 18.09.17.
 */

public class SettingsActivity extends LocationActivity
        implements BluetoothMessage.BluetoothMessageListener, ChooseDeviceView {

    @BindView(R.id.tv_device_title)
    TextView mTvDeviceTitle;
    @BindView(R.id.tv_device_address)
    TextView mTvDeviceAddress;
    @BindView(R.id.btn_on_device)
    Button mBtnOnDevice;
    @BindView(R.id.btn_off_device)
    Button mBtnOffDevice;
    @BindView(R.id.btn_change_name)
    Button mBtnChangeName;
    @BindView(R.id.cl)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.tv_change_password)
    TextView mTvChangePassword;
    @BindView(R.id.btn_change_password)
    Button mBtnChangePassword;
    @BindView(R.id.tv_reset)
    TextView mTvReset;
    @BindView(R.id.btn_reset_controller)
    Button mBtnResetController;
    @BindView(R.id.cb_auto_mode)
    CheckBox mCbAutoMode;
    @BindView(R.id.cb_manual_mode)
    CheckBox mCbManualMode;
    @BindView(R.id.til_latitude)
    TextInputLayout mTilLatitude;
    @BindView(R.id.actv_latitude)
    AutoCompleteTextView mActvLatitude;
    @BindView(R.id.til_longitude)
    TextInputLayout mTilLongitude;
    @BindView(R.id.actv_longitude)
    AutoCompleteTextView mActvLongitude;
    @BindView(R.id.cb_set_coordinates_by_hand)
    CheckBox mCbSetCoordinatesByHand;
    @BindView(R.id.til_timezone)
    TextInputLayout mTilTimezone;
    @BindView(R.id.actv_timezone)
    AutoCompleteTextView mActvTimezone;
    @BindView(R.id.cb_set_timezone_by_hand)
    CheckBox mCbSetTimezoneByHand;
    @BindView(R.id.rl)
    RelativeLayout mRl;
    @BindView(R.id.btn_sync_geolocation)
    Button mBtnSyncGeolocation;

    private BluetoothModule mBluetoothModule;
    private Activity mActivity;
    private SettingsPresenter mSettingsPresenter;
    private BluetoothMessage mBluetoothMessage;
    private String mStatus;
    private AlertDialog mDialog;
    private String mNewDeviceTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initMain();
        start();
    }

    @Override
    public void init() {
        mScrollView.fullScroll(View.FOCUS_UP);
        mActivity = this;
        mDialog = DialogHelper.showProgressBar(this, "Считывание данных");
        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        setMessage(BluetoothCommands.STATUS);

        mBluetoothModule = BluetoothModule.createBluetoothModule(this, this);

        mCbSetCoordinatesByHand.setSelected(false);
        mCbSetTimezoneByHand.setSelected(false);

        mActivity = this;
        mSettingsPresenter = new SettingsPresenter(this);

        mActvTimezone.setText(getTimeZone() + "");

        String[] device = BluetoothHelper.getBluetoothUser(getApplicationContext());
        mTvDeviceAddress.setText(device[0]);
        mTvDeviceTitle.setText(device[1]);
    }

    @Override
    public void setClickListeners() {

        setChangePasswordAndUsername();

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

        mCbAutoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckedBox(mCbAutoMode);
                setMessage(BluetoothCommands.MANUAL_OFF);
            }
        });
        mCbManualMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckedBox(mCbManualMode);
                setMessage(BluetoothCommands.MANUAL_ON);
            }
        });
        mBtnResetController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage(BluetoothCommands.RESET);
                mBluetoothModule.connectDevice(null);
            }
        });
        mSettingsPresenter.setCheckBoxLocation(mCbSetCoordinatesByHand, mTilLatitude, mTilLongitude);
        mSettingsPresenter.setCheckBoxTimezone(mCbSetTimezoneByHand, mTilTimezone);

        mBtnSyncGeolocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActvLatitude.setText(String.valueOf(mCurrentLatitude));
                mActvLongitude.setText(String.valueOf(mCurrentLongitude));
                mActvTimezone.setText(String.valueOf(getTimeZone()));
                CacheHelper.setCoordinatesAndTimezone(getApplicationContext(), mCurrentLongitude, mCurrentLatitude,
                        Integer.parseInt(mActvTimezone.getText().toString()));
            }
        });
    }

    private void setCheckedBox(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
    }

    private void setChangePasswordAndUsername() {
        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_password, null);

                final EditText mPasswordView = (EditText) dialogView.findViewById(R.id.et_password);
                AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle(getString(R.string.input_password))
                        .setView(dialogView)

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                setMessage(BluetoothCommands.SET_PASSWORD,
                                        BluetoothCommands.setPassword(mPasswordView.getText().toString()));
                                ResponseView.showSnackbar(mRl,
                                        ResponseView.SET_PASSWORD);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                passwordDialogBuilder.show();
            }
        });
        mBtnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_password, null);


                TextInputLayout textInputLayout = dialogView.findViewById(R.id.til_password);
                textInputLayout.setHint("Новое название для устройства");
                final EditText mPasswordView = (EditText) dialogView.findViewById(R.id.et_password);
                mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT);


                AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(SettingsActivity.this)
                        // .setTitle(getString(R.string.input_password))
                        .setView(dialogView)

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ResponseView.showSnackbar(mRl,
                                        ResponseView.SET_PASSWORD);
                                setMessage(BluetoothCommands.SET_NAME,
                                        BluetoothCommands.setName(mPasswordView.getText().toString()));
                               mNewDeviceTitle = mPasswordView.getText().toString();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                passwordDialogBuilder.show();
            }
        });
    }

    private void setMessage(String status) {
        mStatus = status;
        mBluetoothMessage.writeMessage(mActivity, status);
    }

    private void setMessage(String status, String text) {
        mStatus = status;
        mBluetoothMessage.writeMessage(mActivity, text);
    }

    @Override
    public void setTag() {
        TAG = "SettingsActivity";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_settings);
        searchItem.setTitle("Сохранить");
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String g = mActvTimezone.getText().toString();
                CacheHelper.setCoordinatesAndTimezone(getApplicationContext(),
                        mCurrentLongitude, mCurrentLatitude,
                        Integer.parseInt(mActvTimezone.getText().toString()));

                finish();
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResponse(String answer) {
        Log.d(TAG, " " + answer);

        if(mStatus!= null) {

            switch (mStatus) {
                case BluetoothCommands.SET_PASSWORD:
                   // mBluetoothMessage.iAmBusy = false;
                    break;

                case BluetoothCommands.SET_NAME:
                 //   mBluetoothMessage.iAmBusy = false;
                    mTvDeviceTitle.setText(mNewDeviceTitle);
                    BluetoothHelper.saveBluetoothDeviceTitle(getApplicationContext(), mNewDeviceTitle);
                    break;

                case BluetoothCommands.STATUS:
                 //   mBluetoothMessage.iAmBusy = false;
                    parseStatus(answer);
                    DialogHelper.hideProgressBar(mDialog);
                    break;

                case BluetoothCommands.RESET:
                    //mTvReset.setText(answer);
                   // mBluetoothMessage.iAmBusy = false;

                    break;

                case BluetoothCommands.ON:
                   // mBluetoothMessage.iAmBusy = false;
                    setDeviceModeColor(false);
                    /*ResponseView.showSnackbar(mRl,
                            ResponseView.ON);*/
                    setMessage(BluetoothCommands.STATUS);

                    break;
                case BluetoothCommands.OFF:
                   // mBluetoothMessage.iAmBusy = false;
                    setDeviceModeColor(true);
                    //ResponseView.showSnackbar(mRl, ResponseView.OFF);
                   // mBluetoothMessage.iAmBusy = false;
                    setMessage(BluetoothCommands.STATUS);
                    break;

                case BluetoothCommands.MANUAL_ON:
                  //  mBluetoothMessage.iAmBusy = false;
                    setMessage(BluetoothCommands.STATUS);
                    mCbAutoMode.setChecked(false);
                    mCbManualMode.setChecked(true);
                    break;

                case BluetoothCommands.MANUAL_OFF:
                  //  mBluetoothMessage.iAmBusy = false;
                    setMessage(BluetoothCommands.STATUS);
                    mCbAutoMode.setChecked(true);
                    mCbManualMode.setChecked(false);
                    break;
            }

        }
    }

    private void parseStatus(String status){
        String[] parameters = status.replaceAll("\r","").split("\n");
        for(String s: parameters){

            if (s.contains("Manual")){
                setMode(s.split(" ")[1]);
            }else
            if (s.contains("Rele")){
                setOnOff(s.split(" ")[1]);
            }/*else if(!s.contains("s") && !s.contains(" ")){
                getTime(s);
            }*/
        }
    }

    private void setMode(String text){
        if(text.equals("Off")){
            mCbAutoMode.setChecked(true);
            mCbManualMode.setChecked(false);
        }else {
            mCbAutoMode.setChecked(false);
            mCbManualMode.setChecked(true);
        }
    }
    private void setDeviceTitle(){
        mTvDeviceAddress.setText(BluetoothHelper.getBluetoothUser(getApplicationContext())[0].trim());
        mTvDeviceTitle.setText(BluetoothHelper.getBluetoothUser(getApplicationContext())[1].trim());
    }

    private void setOnOff(String onOf){
        if (onOf.equals("On")){
            setDeviceModeColor(false);
        }else if (onOf.equals("Off")){
            setDeviceModeColor(true);
        }
    }

    private void setDeviceModeColor(boolean isOn){
        Drawable color1 = getResources().getDrawable(R.drawable.btn_off);
        Drawable color2 = getResources().getDrawable(R.drawable.btn_on);
        if(isOn) {
            mBtnOnDevice.setBackground(color1);
            mBtnOffDevice.setBackground(color2);
        }else {
            mBtnOnDevice.setBackground(color2);
            mBtnOffDevice.setBackground(color1);
        }
    }

    @Override
    public void error(String message) {

    }

    @Override
    public void goNext() {
        ResponseView.showSnackbar(mRl, ResponseView.RESET);
    }

    @Override
    public void addDevice(String text) {

    }

    @Override
    public void setLonLat(double lat, double lon) {
        mActvLatitude.setText(String.valueOf(lat));
        mActvLongitude.setText(String.valueOf(lon));
    }

    @Override
    public boolean setScheduleGeneration() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mBluetoothModule != null) {
            mBluetoothModule.unregister();
        }
    }
}