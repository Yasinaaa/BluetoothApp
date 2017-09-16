package ru.android.bluetooth.one_day;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.root.RootActivity;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ChangeOneDayScheduleActivity extends RootActivity {

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

    private ChangeOneDaySchedulePresenter mChangeOneDaySchedulePresenter;
    private String mStatusSunrise = "Восход мин.:";
    private String mStatusSunset = "Закат мин.:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.generate_schedule);
        setContentView(R.layout.activity_change_choosed_day);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void init() {
        mChangeOneDaySchedulePresenter = new ChangeOneDaySchedulePresenter(this);
        mChangeOneDaySchedulePresenter.setOnClickListenerImageButton(mIbSunrise, mTvSunrise, mTvSunriseMin, mStatusSunrise);
        mChangeOneDaySchedulePresenter.setOnClickListenerImageButton(mIbSunset, mTvSunset, mTvSunsetMin, mStatusSunset);
        mChangeOneDaySchedulePresenter.setNotAvailableDialog(mFabSave);
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
