package ru.android.bluetooth.settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.security.Provider;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.fabric.sdk.android.services.settings.SettingsRequest;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.main.MainActivity;
import ru.android.bluetooth.main.helper.ResponseView;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;
import ru.android.bluetooth.utils.CacheHelper;
import ru.android.bluetooth.utils.DialogHelper;

/**
 * Created by yasina on 18.09.17.
 */

public class SettingsActivity extends RootActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, BluetoothMessage.BluetoothMessageListener {

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

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    private Activity mActivity;
    private SettingsPresenter mSettingsPresenter;
    private BluetoothMessage mBluetoothMessage;
    private String mStatus;
    private AlertDialog mDialog;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 12;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        start();
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(this.getClass().getSimpleName(), "onResume");

        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");
//        alertDialog.dismiss();
        //  alertDialog.cancel();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    Location location;
    AlertDialog alertDialog;

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(SettingsActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);

            return;
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Местопложение")
                    .setMessage("Разрешите включить ваше местоположение")
                    .setPositiveButton(mActivity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(mActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog = dialog.create();
            dialog.show();


        } else {

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            CacheHelper.setCoordinatesAndTimezone(getApplicationContext(), currentLongitude, currentLongitude, getTimeZone());
            mActvLatitude.setText(String.valueOf(currentLatitude));
            mActvLongitude.setText(String.valueOf(currentLongitude));


            //Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        mActvLatitude.setText(String.valueOf(currentLatitude));
        mActvLongitude.setText(String.valueOf(currentLongitude));
        //Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
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
