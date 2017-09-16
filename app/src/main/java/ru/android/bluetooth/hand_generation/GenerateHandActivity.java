package ru.android.bluetooth.hand_generation;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.root.RootActivity;

/**
 * Created by itisioslab on 03.08.17.
 */

public class GenerateHandActivity  extends RootActivity  {

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

    private GenerateHandPresenter mGenerateHandPresenter;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunrise_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void init() {
       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);*/

        mGenerateHandPresenter = new GenerateHandPresenter(this);
        mGenerateHandPresenter.setOnClickListenerImageButton(mIbChangeStartTime, mTvSunrise);
        mGenerateHandPresenter.setOnClickListenerImageButton(mIbChangeFinishTime, mTvSunset);
        mGenerateHandPresenter.setCheckBoxLocation(mCbSetCoordinatesByHand, mTilLatitude, mTilLongitude);
        mGenerateHandPresenter.setCheckBoxLocation(mCbSetTimezoneByHand, mTilTimezone);
        mGenerateHandPresenter.setNotAvailableDialog(mFab);
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
}
