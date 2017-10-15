package ru.android.autorele.calendar.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import ru.android.autorele.R;
import ru.android.autorele.calendar.module.ChangeChoosedDayModule;
import ru.android.autorele.calendar.presenter.ChangeChoosedDayPresenter;
import ru.android.autorele.common.date_time.DateParser;
import ru.android.autorele.root.RootActivity;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ChangeChoosedDayActivity extends RootActivity
        implements ChangeChoosedDayModule.View {

    public static String DAY_LOG = "DAY";
    public static String ON_LOG = "ON";
    public static String OFF_LOG = "OFF";
    public static int REQUEST_CODE = 12223;

    @BindView(R.id.tv_sunrise)
    TextView mTvSunrise;
    @BindView(R.id.ib_change_sunrise_time)
    ImageButton mIbSunrise;
    @BindView(R.id.tv_sunset)
    TextView mTvSunset;
    @BindView(R.id.ib_change_sunset_time)
    ImageButton mIbSunset;
    @BindView(R.id.tv_sunrise_min)
    TextView mTvSunriseMin;
    @BindView(R.id.tv_sunset_min)
    TextView mTvSunsetMin;
    @BindView(R.id.fab_save_one_day_schedule)
    FloatingActionButton mFabSave;
    @BindView(R.id.tv_current_day)
    TextView mTvCurentDay;

    private ChangeChoosedDayPresenter mChangeOneDaySchedulePresenter;

    private String mDay, mOnTime, mOffTime;
    private DateParser mDateParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_choosed_day);
        start();
    }

    @Override
    public void init() {

        mDay = getIntent().getStringExtra(DAY_LOG);
        mOnTime = getIntent().getStringExtra(ON_LOG);
        mOffTime = getIntent().getStringExtra(OFF_LOG);
        mDateParser = new DateParser();

        mTvCurentDay.setText(mDay);
        setSunriseTime(mOnTime, String.valueOf(mDateParser.getNumTime(mOnTime)));
        setSunsetTime(mOffTime, String.valueOf(mDateParser.getNumTime(mOffTime)));
        mChangeOneDaySchedulePresenter = new ChangeChoosedDayPresenter(this, this);
    }

    @Override
    public void setClickListeners() {
        mFabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeOneDaySchedulePresenter.onFabClickListener();
            }
        });

        mIbSunrise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeOneDaySchedulePresenter.setOnClickListenerImageButton(ChangeChoosedDayPresenter.TYPE1);
            }
        });

        mIbSunset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeOneDaySchedulePresenter.setOnClickListenerImageButton(ChangeChoosedDayPresenter.TYPE2);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void setTag() {
        TAG = "ChangeChoosedDayActivity";
    }

    @Override
    public String getSunriseTimeValue() {
        return mTvSunrise.getText().toString();
    }

    @Override
    public String getSunsetTimeValue() {
        return mTvSunset.getText().toString();
    }

    @Override
    public void setSunriseTime(String timeView, String minView) {
        mTvSunrise.setText(timeView);
        mTvSunriseMin.setText(getString(R.string.sunrise_min) + " " + minView);
    }

    @Override
    public void setSunsetTime(String timeView, String minView) {
        mTvSunset.setText(timeView);
        mTvSunsetMin.setText(getString(R.string.sunset_min)  + " " + minView);
    }
}
