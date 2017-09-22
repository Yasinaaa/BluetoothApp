package ru.android.bluetooth.hand_generation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.root.RootActivity;

/**
 * Created by itisioslab on 03.08.17.
 */

public class GenerateHandActivity extends RootActivity  {

    @BindView(R.id.tv_sunrise)
    TextView mTvSunrise;
    @BindView(R.id.ib_change_sunrise_time)
    ImageButton mIbChangeSunriseTime;
    @BindView(R.id.tv_sunset)
    TextView mTvSunset;
    @BindView(R.id.ib_change_sunset_time)
    ImageButton mIbChangeSunsetTime;
    @BindView(R.id.til_repeat)
    TextInputLayout mTilRepeat;
    @BindView(R.id.actv_repeat)
    AutoCompleteTextView mActvRepeat;
    @BindView(R.id.tv_repeat_begin_day)
    TextView mTvRepeatBeginDay;

    @BindView(R.id.ib_set_begin_day)
    Button mIbSetBeginDay;
    @BindView(R.id.tv_repeat_end_day)
    TextView mTvRepeatEndDay;
    @BindView(R.id.ib_set_end_day)
    Button mIbSetEndDay;
    @BindView(R.id.fab_save_one_day_schedule)
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
        setContentView(R.layout.generate_schedule);
        start();
    }

    @Override
    public void init() {

        mGenerateHandPresenter = new GenerateHandPresenter(this);
        mGenerateHandPresenter.setOnClickListenerImageButton(true, mIbChangeSunriseTime, mTvSunrise);
        mGenerateHandPresenter.setOnClickListenerImageButton(false, mIbChangeSunsetTime, mTvSunset);
        mGenerateHandPresenter.setOnDateClickListenerImageButton(true, mIbSetBeginDay, mTvRepeatBeginDay);
        mGenerateHandPresenter.setOnDateClickListenerImageButton(false, mIbSetEndDay, mTvRepeatEndDay);
        /*mGenerateHandPresenter.setCheckBoxLocation(mCbSetCoordinatesByHand, mTilLatitude, mTilLongitude);
        mGenerateHandPresenter.setCheckBoxLocation(mCbSetTimezoneByHand, mTilTimezone);*/
        mGenerateHandPresenter.setOnFabClickListener(mFab);
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

    /*private void getValues(){
        mTvSunrise.getText();
        mTvSunset.getText();
    }*/
}
