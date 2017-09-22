package ru.android.bluetooth.settings;

/**
 * Created by yasina on 22.09.17.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import ru.android.bluetooth.BuildConfig;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.main.helper.ResponseView;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.utils.BluetoothHelper;
import ru.android.bluetooth.utils.CacheHelper;
import ru.android.bluetooth.utils.DialogHelper;

public class SettingsActivity extends RootActivity implements BluetoothMessage.BluetoothMessageListener{

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;

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

    private double currentLatitude;
    private double currentLongitude;
    private Activity mActivity;
    private SettingsPresenter mSettingsPresenter;
    private BluetoothMessage mBluetoothMessage;
    private String mStatus;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        start();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
    @Override
    public void init() {
        mScrollView.fullScroll(View.FOCUS_UP);
        //mCoordinatorLayout.scrollTo(0,0);
        mActivity = this;
        mDialog = DialogHelper.showProgressBar(this, "Считывание данных");
        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        setMessage(BluetoothCommands.STATUS);

        mCbSetCoordinatesByHand.setSelected(false);
        mCbSetTimezoneByHand.setSelected(false);

        mActivity = this;
        mSettingsPresenter = new SettingsPresenter(this);

        mActvTimezone.setText(getTimeZone() + "");

        String[] device = BluetoothHelper.getBluetoothUser(getApplicationContext());
        mTvDeviceAddress.setText(device[0]);
        mTvDeviceTitle.setText(device[1]);
        // mCoordinatorLayout.scrollTo(0,0);

    }


    private int getTimeZone() {
        Calendar mCalendar = Calendar.getInstance();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        return (int) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
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
            }
        });
        mSettingsPresenter.setCheckBoxLocation(mCbSetCoordinatesByHand, mTilLatitude, mTilLongitude);
        mSettingsPresenter.setCheckBoxTimezone(mCbSetTimezoneByHand, mTilTimezone);

        mBtnSyncGeolocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActvLatitude.setText(String.valueOf(currentLatitude));
                mActvLongitude.setText(String.valueOf(currentLongitude));
                mActvTimezone.setText(String.valueOf(getTimeZone()));
                CacheHelper.setCoordinatesAndTimezone(getApplicationContext(), currentLongitude, currentLatitude,
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
                textInputLayout.setHint("Новое название устройства");
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
                                mTvDeviceTitle.setText(mPasswordView.getText().toString());

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
                CacheHelper.setCoordinatesAndTimezone(getApplicationContext(), currentLongitude, currentLatitude,
                        Integer.parseInt(mActvTimezone.getText().toString()));

                finish();
                return false;
            }
        });
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }


    AlertDialog alertDialog;
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        final Activity a = this;

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            currentLatitude = mLastLocation.getLatitude();
                            currentLongitude = mLastLocation.getLongitude();

                            mActvLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
                            mActvLongitude.setText(String.valueOf(mLastLocation.getLongitude()));

                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());

                            AlertDialog.Builder dialog = new AlertDialog.Builder(a)
                                    .setTitle("Местопложение")
                                    .setMessage("Разрешите включить ваше местоположение")
                                    .setPositiveButton(a.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            alertDialog.dismiss();
                                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        }
                                    })
                                    .setNegativeButton(a.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            alertDialog.dismiss();
                                        }
                                    });
                            alertDialog = dialog.create();
                            dialog.show();
                        }
                    }
                });
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(SettingsActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            startLocationPermissionRequest();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
    @Override
    public void onResponse(String answer) {
        Log.d(TAG, " " + answer);

        if(mStatus!= null) {

            switch (mStatus) {
                case BluetoothCommands.SET_PASSWORD:

                    break;

                case BluetoothCommands.SET_NAME:

                    break;

                case BluetoothCommands.STATUS:

                    parseStatus(answer);
                    DialogHelper.hideProgressBar(mDialog);
                    break;

                case BluetoothCommands.RESET:
                    //mTvReset.setText(answer);
                    ResponseView.showSnackbar(mRl, ResponseView.RESET);
                    break;

                case BluetoothCommands.ON:
                    setDeviceModeColor(false);
                    /*ResponseView.showSnackbar(mRl,
                            ResponseView.ON);*/
                    setMessage(BluetoothCommands.STATUS);

                    break;
                case BluetoothCommands.OFF:
                    setDeviceModeColor(true);
                    //ResponseView.showSnackbar(mRl, ResponseView.OFF);
                    setMessage(BluetoothCommands.STATUS);
                    break;

                case BluetoothCommands.MANUAL_ON:
                    setMessage(BluetoothCommands.STATUS);
                    mCbAutoMode.setChecked(false);
                    mCbManualMode.setChecked(true);
                    break;

                case BluetoothCommands.MANUAL_OFF:
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


}
