package ru.android.bluetooth.one_day;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.common.DateParser;
import ru.android.bluetooth.root.RootActivity;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ChangeOneDayScheduleActivity extends RootActivity {

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

    private ChangeOneDaySchedulePresenter mChangeOneDaySchedulePresenter;
    private String mStatusSunrise = "Восход мин.:";
    private String mStatusSunset = "Закат мин.:";
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
        mTvSunrise.setText(mOnTime);
        mTvSunset.setText(mOffTime);
        mTvSunriseMin.setText(mStatusSunrise + " " + String.valueOf(mDateParser.getNumTime(mOnTime)));
        mTvSunsetMin.setText(mStatusSunset + " " +  String.valueOf(mDateParser.getNumTime(mOffTime)));

        mChangeOneDaySchedulePresenter = new ChangeOneDaySchedulePresenter(this);
        mChangeOneDaySchedulePresenter.setOnClickListenerImageButton(mIbSunrise, mTvSunrise, mTvSunriseMin, mStatusSunrise);
        mChangeOneDaySchedulePresenter.setOnClickListenerImageButton(mIbSunset, mTvSunset, mTvSunsetMin, mStatusSunset);
        //mChangeOneDaySchedulePresenter.setNotAvailableDialog(mFabSave);
    }

    @Override
    public void setClickListeners() {
        mFabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(ON_LOG, mDateParser.getNumTime(mTvSunrise.getText().toString()));
                intent.putExtra(OFF_LOG, mDateParser.getNumTime(mTvSunset.getText().toString()));
                setResult(RESULT_OK, intent);
                finish();
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

    }


}
