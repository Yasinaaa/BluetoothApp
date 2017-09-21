package ru.android.bluetooth.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.DateParser;
import ru.android.bluetooth.one_day.ChangeOneDayScheduleActivity;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.CacheHelper;

/**
 * Created by itisioslab on 03.08.17.
 */

public class CalendarActivity extends RootActivity
        implements CalendarModule.View, CalendarModule.OnItemClicked,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int REQUEST_GEOLOCATION_PERMISSION = 784;
    private static final int REQUEST_READ_PERMISSION = 785;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @BindView(R.id.tableLayout)
    TableLayout mTableLayout;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.fab_menu)
    FloatingActionsMenu fabMenu;

    private CalendarPresenter mCalendarPresenter;
    private BluetoothMessage mBluetoothMessage;
    private String mDayToChange = "";
    private String mOnDay = "";
    private String mOffDay = "";
    private int id;
    private AlertDialog alertDialog, alertDialog2;
    private Calendar mStartDate, mFinishDate;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private boolean isScheduleGeneration = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_calendar);
        setContentView(R.layout.activity_schedule_neee);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //((App) getApplication()).getComponent().inject(this);
        //ButterKnife.bind(this);
        // init();
        start();
    }


    public void fabClicked(View v) {
        switch (v.getId()) {
            case R.id.fab_generate_shedule_hand_one_day:

                if (!mDayToChange.equals("")) {
                    Intent intent = new Intent(CalendarActivity.this, ChangeOneDayScheduleActivity.class);
                    intent.putExtra(ChangeOneDayScheduleActivity.DAY_LOG, mDayToChange);
                    intent.putExtra(ChangeOneDayScheduleActivity.ON_LOG, mOnDay);
                    intent.putExtra(ChangeOneDayScheduleActivity.OFF_LOG, mOffDay);
                    startActivityForResult(intent, ChangeOneDayScheduleActivity.REQUEST_CODE);
                    fabMenu.collapse();
                    fabMenu.collapseImmediately();
                } else {
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(CalendarActivity.this)
                            .setTitle("Ошибка")
                            .setMessage("Вы не выбрали день")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                                    //alertDialog2.cancel();
                                    fabMenu.collapse();
                                    fabMenu.collapseImmediately();
                                }
                            });
                    //alertDialog2 = dialog2.create();
                    dialog2.show();
                }


                break;
            case R.id.fab_load_schedule:
                //ActivityHelper.startActivity(CalendarActivity.this, GenerateHandActivity.class);
                requestWritePermission();
                break;

            case R.id.fab_generate_schedule_sunrise_set:
                //ActivityHelper.startActivity(CalendarActivity.this, ScheduleGeneratorActivity.class);

                setSchedule();
                fabMenu.collapseImmediately();
                fabMenu.setSelected(false);

               //mScheduleBluetoothReader.readSchedule(Calendar.getInstance(), finishDate);
               /*mStatus = BluetoothCommands.SET_DATA;
               mAlertDialog = ActivityHelper.showProgressBar(mActivity, getString(R.string.generate_schedule));
               mBluetoothMessage.writeMessage(onList, offList);*/
               break;
       }
    }

    private void setSchedule(){

        String[] result = CacheHelper.getCoordinatesAndTimezone(getApplicationContext());
        if (result != null) {
            mCalendarPresenter.generateSchedule(mStartDate, mFinishDate,
                    Double.parseDouble(result[0]), Double.parseDouble(result[1]),
                    Integer.parseInt(result[2]));
            fabMenu.collapseImmediately();
            fabMenu.setSelected(false);
        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{
                                            Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GEOLOCATION_PERMISSION);
                                } else {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                                isScheduleGeneration = true;
                                alertDialog.dismiss();
                            }
                        });
                alertDialog = dialog.create();
                dialog.show();
            }
        }
    }

    public void requestWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            mCalendarPresenter.setLoadSchedule();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCalendarPresenter.setLoadSchedule();
        }

    }

    @Override
    public void init(){

        mStartDate = Calendar.getInstance();
        mFinishDate = Calendar.getInstance();
        mFinishDate.add(Calendar.YEAR, 1);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();

        mCalendarPresenter = new CalendarPresenter(this, mBluetoothMessage, this, new DateParser(mStartDate, getApplicationContext()),
                this);
        mCalendarPresenter.getSchedule();

        frameLayout.getBackground().setAlpha(0);

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {

                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();

                        return true;
                    }
                });
                frameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabClicked(view);

                    }
                });
            }


            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                //frameLayout.setOnTouchListener(null);

            }
        });
    }

    @Override
    public void setClickListeners() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void setTag() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String date) {
                String parts[] = date.split("\\.");

                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                mCalendarPresenter.searchDay(date, mTableLayout, mNestedScrollView);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String date) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.cancelLongPress();
                return false;
            }
        });
        return true;
    }


    @Override
    public void onLoadingScheduleFinished() {
        mCalendarPresenter.setTable(mTableLayout);
    }

    @Override
    public void onItemClick(int id, String text, String on, String off) {
        this.id = id;
        mDayToChange = text;
        mOnDay = on;
        mOffDay = off;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChangeOneDayScheduleActivity.REQUEST_CODE){

            int on = data.getIntExtra(ChangeOneDayScheduleActivity.ON_LOG, 0);
            int off = data.getIntExtra(ChangeOneDayScheduleActivity.OFF_LOG, 0);
            mCalendarPresenter.generateSchedule(id, on, off);
        }

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

    private int getTimeZone(){
        Calendar mCalendar = Calendar.getInstance();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        return (int) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
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
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"check =");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            /*LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Местопложение")
                    .setMessage("Разрешите включить ваше местоположение")
                    .setNegativeButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            alertDialog.dismiss();
                        }
                    });
            alertDialog = dialog.create();
            dialog.show();*/


        } else {
            Log.d(TAG,"open =");
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            CacheHelper.setCoordinatesAndTimezone(getApplicationContext(), currentLongitude, currentLongitude, getTimeZone());
            if(isScheduleGeneration){
                alertDialog = ActivityHelper.showProgressBar(this, "Запись изменений");
                Log.d(TAG,"send =");
                setSchedule();
                isScheduleGeneration = false;

            }
        }
    }
}
