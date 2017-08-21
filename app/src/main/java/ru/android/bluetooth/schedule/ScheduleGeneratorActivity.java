package ru.android.bluetooth.schedule;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.schedule.helper.ScheduleBluetoothReader;
import ru.android.bluetooth.schedule.helper.ScheduleGenerator;

/**
 * Created by itisioslab on 08.08.17.
 */

public class ScheduleGeneratorActivity extends RootActivity implements BluetoothMessage.BluetoothMessageListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String TAG = ScheduleGeneratorActivity.class.getName();

    int timerBehaviour = 0;
    public double JD = 0;
    public int zone = 0;
    public boolean dst = false;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private BluetoothMessage mBluetoothMessage;
    private ScheduleBluetoothReader mScheduleBluetoothReader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        mScheduleBluetoothReader = new ScheduleBluetoothReader(mBluetoothMessage);
        Calendar finishDate = Calendar.getInstance();
        finishDate.add(Calendar.YEAR, 1);

        //mBluetoothMessage.writeMessage(BluetoothCommands.STATUS);
        mScheduleBluetoothReader.readSchedule(Calendar.getInstance(), finishDate);
        //mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
    }



    private void generateSchedule(Calendar startDate, Calendar endDate, double latitude, double longitude){
        Calendar currentDate = Calendar.getInstance();
        double sunRise;
        double sunSet;
        int d = 0;
        while(startDate.compareTo(endDate) <= 0){
            currentDate = startDate;

            JD = ScheduleGenerator.calcJD(startDate);  //OR   JD = Util.calcJD(2014, 6, 1);
            sunRise = ScheduleGenerator.calcSunRiseUTC(JD, latitude, longitude);
            sunSet = ScheduleGenerator.calcSunSetUTC(JD, latitude, longitude);

            d++;
            Log.d(TAG,"day=" + d + " " +
                    ScheduleGenerator.getTimeString(false,sunRise, zone, JD, dst) + " " +
                    ScheduleGenerator.getTimeString(false,sunSet, zone, JD, dst)+ " " +
                    ScheduleGenerator.getTimeString(true,sunRise, zone, JD, dst)+ " " +
                    ScheduleGenerator.getTimeString(true,sunSet, zone, JD, dst)
            );
            startDate.add(Calendar.DAY_OF_YEAR, 1);

        }
    }

    @Override
    public void onResponse(String answer) {
      //  Log.d(TAG, answer);
        mScheduleBluetoothReader.addItem(answer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");


        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }


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

        } else {

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            Calendar finishDate = Calendar.getInstance();
            finishDate.add(Calendar.YEAR, 1);
            generateSchedule(Calendar.getInstance(), finishDate, currentLatitude, currentLongitude);

            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
