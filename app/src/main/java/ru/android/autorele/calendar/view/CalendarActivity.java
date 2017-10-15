package ru.android.autorele.calendar.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import ru.android.autorele.R;
import ru.android.autorele.bluetooth.BluetoothCommands;
import ru.android.autorele.bluetooth.BluetoothMessage;
import ru.android.autorele.calendar.module.CalendarModule;
import ru.android.autorele.calendar.Day;
import ru.android.autorele.calendar.MonthAdapter;
import ru.android.autorele.calendar.presenter.CalendarPresenter;
import ru.android.autorele.calendar.presenter.FilePresenter;
import ru.android.autorele.common.date_time.DateParser;
import ru.android.autorele.common.location.LocationActivity;
import ru.android.autorele.main.helper.ScheduleLoading;
import ru.android.autorele.utils.ActivityHelper;
import ru.android.autorele.utils.CacheHelper;

/**
 * Created by itisioslab on 03.08.17.
 */

public class CalendarActivity extends LocationActivity
        implements CalendarModule.View, CalendarModule.OnItemClicked, ScheduleLoading.View {

    private static final int REQUEST_WRITE_PERMISSION = 786;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.fab_menu)
    FloatingActionsMenu mFloatingActionsMenu;

    private FilePresenter mFilePresenter;
    private CalendarPresenter mCalendarPresenter;
    private MonthAdapter mMonthAdapter;
    private BluetoothMessage mBluetoothMessage;
    private Calendar mStartDate, mFinishDate;
    private ScheduleLoading scheduleLoading;

    private String mDayToChange = "";
    private String mOnDay = "";
    private String mOffDay = "";
    private int mDayOfYear, mDayOfMonth;
    Calendar clickedDate;
    DateParser mDateParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initMain();
        start();
    }

    public void onFabClicked(View v) {
        switch (v.getId()) {
            case R.id.fab_download_schedule:
                mFilePresenter.setChooseFileDialog();
                break;

            case R.id.fab_save_schedule:
                mCalendarPresenter.generateSchedule();
                break;

            case R.id.fab_generate_shedule_hand_one_day:
                mCalendarPresenter.changeSelectedDay(mDayToChange, clickedDate, mOnDay, mOffDay);
                break;

            case R.id.fab_load_schedule:
                requestWritePermission();
                break;

            case R.id.fab_generate_schedule_sunrise_set:
                mIsScheduleGeneration = true;
                String[] data = CacheHelper.getCoordinatesAndTimezone(getApplicationContext());
                if (data != null){
                    mCalendarPresenter.generateSchedule(mStartDate, mFinishDate,
                            Double.parseDouble(data[0]),
                            Double.parseDouble(data[1]),
                            Integer.parseInt(data[3]));
                }else {
                    updateLocation();
                }
                break;
        }
    }

    @Override
    public void collapseFab(){
        mFloatingActionsMenu.collapse();
        mFloatingActionsMenu.collapseImmediately();
    }

    public void requestWritePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }else {
            mCalendarPresenter.setLoadSchedule();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCalendarPresenter.setLoadSchedule();
        }
        if (requestCode == ActivityHelper.REQUEST_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mFilePresenter.openIntent();
        }
    }

    @Override
    public void init(){

        mStartDate = Calendar.getInstance();
        mStartDate.set(Calendar.DAY_OF_MONTH, 1);
        mStartDate.set(Calendar.MONTH, Calendar.JANUARY);

        mFinishDate = Calendar.getInstance();
        mFinishDate.set(Calendar.DAY_OF_MONTH, 31);
        mFinishDate.set(Calendar.MONTH, Calendar.DECEMBER);

        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();

        mFilePresenter = new FilePresenter(this);
        mCalendarPresenter = new CalendarPresenter(this, mBluetoothMessage, this);
        mCalendarPresenter.getSchedule();
        mDateParser = new DateParser();

        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mCoordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mFloatingActionsMenu.collapse();

                        return true;
                    }
                });
                mCoordinatorLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFabClicked(view);

                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
            }
        });

        scheduleLoading = new ScheduleLoading(this, this);
        mMonthAdapter = new MonthAdapter(getSupportFragmentManager(), getApplicationContext());
    }

    @Override
    public void setResult() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public void setScheduleTitle(String title) {
        CacheHelper.setSchedulePath(getApplicationContext(), title);
    }

    @Override
    public void dataCreated(int[] onList, int[] offList) {
        mBluetoothMessage.mStatus = BluetoothCommands.GET_TABLE + "2";
        mBluetoothMessage.writeMessage(this, onList, offList);
    }

    @Override
    public void setClickListeners() {

    }

    @Override
    public void setTag() {
        TAG = "CalendarActivity";
    }


    @Override
    public void onLoadingScheduleFinished() {
        ArrayList<Day> schedule = mCalendarPresenter.setTable();
        collapseFab();

        for (int month=0; month< schedule.size(); month++) {
            Bundle bundle = new Bundle();
            bundle.putInt(CalendarFragment.MONTH, month);
            bundle.putIntArray(CalendarFragment.ON_LIST, schedule.get(month).onTime);
            bundle.putIntArray(CalendarFragment.OFF_LIST, schedule.get(month).offTime);

            CalendarFragment calendarFragment = new CalendarFragment();
            calendarFragment.setArguments(bundle);
            mMonthAdapter.addFragment(calendarFragment);
        }
        mCalendarPresenter.setupViewPager(mTabLayout, mViewPager, mMonthAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onItemClick(int month, int day, String text, String on, String off) {

        clickedDate = mStartDate;
        clickedDate.set(Calendar.MONTH, month);
        clickedDate.set(Calendar.DAY_OF_MONTH, day+1);

        mDayOfYear = clickedDate.get(Calendar.DAY_OF_YEAR);
        mDayOfMonth = day;
        mDayToChange = text;
        mOnDay = on;
        mOffDay = off;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode==RESULT_OK) {
            mFilePresenter.loadSchedule(data, scheduleLoading);
        }

        if (requestCode == ChangeChoosedDayActivity.REQUEST_CODE){

            int on, off;
            try {
                on = data.getIntExtra(ChangeChoosedDayActivity.ON_LOG, 0);
                off = data.getIntExtra(ChangeChoosedDayActivity.OFF_LOG, 0);

                CalendarFragment calendarFragment = mMonthAdapter.getItem(mViewPager.getCurrentItem());
                mCalendarPresenter.saveChanges(mDayOfYear, mDayOfMonth, on, off, calendarFragment);

            }catch (RuntimeException e){

            }
        }
    }

    @Override
    public void setLonLat(double lat, double lon) {
        mIsScheduleGeneration = false;
        mCalendarPresenter.generateSchedule(mStartDate, mFinishDate, lat, lon, getTimeZone());
        mFloatingActionsMenu.collapseImmediately();
        mFloatingActionsMenu.setSelected(false);
    }

    @Override
    public boolean setScheduleGeneration() {
        return false;
    }
}
