package ru.android.bluetooth.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.Manifest;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.DateParser;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.schedule.helper.ScheduleBluetoothReader;
import ru.android.bluetooth.schedule.helper.ScheduleGenerator;
import ru.android.bluetooth.utils.ActivityHelper;

/**
 * Created by itisioslab on 08.08.17.
 */

public class ScheduleGeneratorActivity extends RootActivity
        implements
        ScheduleModule.View,
        BluetoothMessage.BluetoothMessageListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String TAG = ScheduleGeneratorActivity.class.getName();

    @BindView(R.id.tv_sunrise)
    TextView mTvSunrise;
    @BindView(R.id.ib_change_start_time)
    ImageButton mIbChangeStartTime;
    @BindView(R.id.tv_sunset)
    TextView mTvSunset;
    @BindView(R.id.ib_change_finish_time)
    ImageButton mIbChangeFinishTime;
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
    @BindView(R.id.fab_save)
    FloatingActionButton mFab;

    int timerBehaviour = 0;
    public double JD = 0;
    //public int zone = 0;
    public boolean dst = false;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private BluetoothMessage mBluetoothMessage;
    private ScheduleBluetoothReader mScheduleBluetoothReader;
    private SchedulePresenter mSchedulePresenter;
    private DateParser mDateParser;
    private int[] onList = new int[366];
    private int[] offList = new int[366];
    private String mStatus;
    private Activity mActivity;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunrise_set);
        start();
        /*mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);

        Calendar finishDate = Calendar.getInstance();
        finishDate.add(Calendar.YEAR, 1);*/


        // mScheduleBluetoothReader.readSchedule(Calendar.getInstance(), finishDate);

    }

    @Override
    public void init() {

        mActivity = this;
        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        mScheduleBluetoothReader = new ScheduleBluetoothReader(mBluetoothMessage, getApplicationContext(), this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);


        mCbSetCoordinatesByHand.setSelected(false);
        mCbSetTimezoneByHand.setSelected(false);
        Calendar calendar = Calendar.getInstance();
        mDateParser = new DateParser(calendar, getApplicationContext());
                mTvSunrise.setText(calendar.get(Calendar.DAY_OF_MONTH) + ".0" +
                (calendar.get(Calendar.MONTH) + 1) + "." +
                calendar.get(Calendar.YEAR));
        calendar.add(Calendar.YEAR, 1);
        mTvSunset.setText(calendar.get(Calendar.DAY_OF_MONTH) + ".0" +
                (calendar.get(Calendar.MONTH) + 1) + "." +
                calendar.get(Calendar.YEAR));

        mSchedulePresenter = new SchedulePresenter(this);
        mSchedulePresenter.setOnClickListenerImageButton(mIbChangeStartTime, mTvSunrise);
        mSchedulePresenter.setOnClickListenerImageButton(mIbChangeFinishTime, mTvSunset);
        mSchedulePresenter.setCheckBoxLocation(mCbSetCoordinatesByHand, mTilLatitude, mTilLongitude);
        mSchedulePresenter.setCheckBoxLocation(mCbSetTimezoneByHand, mTilTimezone);
        //mSchedulePresenter.setOnFabClickListener(mFab);

        /*Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("z", Locale.getDefault());
        String localTime = date.format(currentLocalTime);
        localTime.substring(localTime.indexOf("+") + 1, )*/

        Calendar mCalendar = Calendar.getInstance();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        mActvTimezone.setText(TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS) + "");
    }

    @Override
    public void setClickListeners() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar finishDate = Calendar.getInstance();
                finishDate.add(Calendar.YEAR, 1);
                generateSchedule(Calendar.getInstance(), finishDate, currentLatitude, currentLongitude);
                mScheduleBluetoothReader.readSchedule(Calendar.getInstance(), finishDate);
                mStatus = BluetoothCommands.SET_DATA;
                mAlertDialog = ActivityHelper.showProgressBar(mActivity, getString(R.string.generate_schedule));
                mBluetoothMessage.writeMessage(onList, offList);

            }
        });
    }

    private void generateSchedule(Calendar startDate, Calendar endDate, double latitude, double longitude) {
        Calendar currentDate = Calendar.getInstance();
        double sunRise;
        double sunSet;
        int d = 0;
        while (startDate.compareTo(endDate) <= 0) {
            currentDate = startDate;

            JD = ScheduleGenerator.calcJD(startDate);  //OR   JD = Util.calcJD(2014, 6, 1);
            sunRise = ScheduleGenerator.calcSunRiseUTC(JD, latitude, longitude);
            sunSet = ScheduleGenerator.calcSunSetUTC(JD, latitude, longitude);

            /*Log.d(TAG,"day=" + d + " " +
                    ScheduleGenerator.getTimeString(false,sunRise, TimeZone.getDefault(), JD, dst) + " " +
                    ScheduleGenerator.getTimeString(false,sunSet, zone, JD, dst)+ " " +
                    ScheduleGenerator.getTimeString(true,sunRise, zone, JD, dst)+ " " +
                    ScheduleGenerator.getTimeString(true,sunSet, zone, JD, dst)
            );*/
            int zone = Integer.parseInt(mActvTimezone.getText().toString());
            onList[d] = Integer.parseInt(ScheduleGenerator.getTimeString(true, sunRise, zone, JD, dst));
            offList[d] = Integer.parseInt(ScheduleGenerator.getTimeString(true, sunSet, zone, JD, dst));
            //onList[d] = 720;
            //offList[d] = 130;
            d++;

            startDate.add(Calendar.DAY_OF_YEAR, 1);

        }

    }

    @Override
    public void onResponse(String answer) {
        Log.d(TAG, answer);
        if(answer.contains("Not command") && answer.contains("Ok\n") && mStatus == BluetoothCommands.SET_DATA){
            ActivityHelper.changeProgressBarText(mAlertDialog, getString(R.string.loading_schedule));
            writeMessage(BluetoothCommands.GET_TABLE);
            String result = mDateParser.setCorrectDateView(Calendar.getInstance());
            mDateParser.saveBeginDay(result);
        }
        if(mStatus == BluetoothCommands.GET_TABLE){
            mScheduleBluetoothReader.addItem(answer);
        }

    }

    private void writeMessage(String status){
        mStatus = status;
        mBluetoothMessage.writeMessage(status);
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


    AlertDialog alertDialog;
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Местопложение")
                    .setMessage("Разрешите включить ваше местоположение")
                    .setNegativeButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    });
                    alertDialog = dialog.create();
                    dialog.show();


        } else {

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
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
            // other 'case' lines to check for other
            // permissions this app might request
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
    public void setTag() {

    }

    @Override
    public void closeProgressBar() {
        ActivityHelper.hideProgressBar(alertDialog);
    }
}
