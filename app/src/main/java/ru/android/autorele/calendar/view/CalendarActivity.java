package ru.android.autorele.calendar.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import ru.android.autorele.calendar.CalendarModule;
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

    public void fabClicked(View v) {
        switch (v.getId()) {
            case R.id.fab_download_schedule:
                mFilePresenter.setChooseFileDialog();
                break;

            case R.id.fab_save_schedule:
                mCalendarPresenter.generateSchedule();
                break;

            case R.id.fab_generate_shedule_hand_one_day:

                if (!mDayToChange.equals("")) {
                    Intent intent = new Intent(CalendarActivity.this, ChangeChoosedDayActivity.class);
                    intent.putExtra(ChangeChoosedDayActivity.DAY_LOG,
                            mDateParser.setZeros(clickedDate.get(Calendar.DAY_OF_MONTH))
                            + "." + mDateParser.setZeros((clickedDate.get(Calendar.MONTH)+1)));
                    intent.putExtra(ChangeChoosedDayActivity.ON_LOG, mOnDay);
                    intent.putExtra(ChangeChoosedDayActivity.OFF_LOG, mOffDay);
                    startActivityForResult(intent, ChangeChoosedDayActivity.REQUEST_CODE);
                    mFloatingActionsMenu.collapse();
                    mFloatingActionsMenu.collapseImmediately();

                } else {
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(CalendarActivity.this)
                            .setTitle("Ошибка")
                            .setMessage("Вы не выбрали день")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mFloatingActionsMenu.collapse();
                                    mFloatingActionsMenu.collapseImmediately();
                                }
                            });
                    dialog2.show();
                }
                break;

            case R.id.fab_load_schedule:
                requestWritePermission();
                break;

            case R.id.fab_generate_schedule_sunrise_set:
                mIsScheduleGeneration = true;
                updateLocation();
                break;
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
        mCalendarPresenter = new CalendarPresenter(this, mBluetoothMessage, this, this);
        mCalendarPresenter.getSchedule();
        mDateParser = new DateParser();


        //mFrameLayout.getBackground().setAlpha(0);

        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                //mFrameLayout.getBackground().setAlpha(240);
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
                        fabClicked(view);

                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
               // mFrameLayout.getBackground().setAlpha(0);
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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }*/

    @Override
    public void setTag() {
        TAG = "CalendarActivity";
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String date) {
                CalendarFragment calendarFragment = mMonthAdapter.getItem(mViewPager.getCurrentItem());
                mCalendarPresenter.searchDay(date, calendarFragment, mViewPager);
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
    }*/


    @Override
    public void onLoadingScheduleFinished() {
        ArrayList<Day> schedule = mCalendarPresenter.setTable();
        mFloatingActionsMenu.collapse();
        mFloatingActionsMenu.collapseImmediately();


        if(mMonthAdapter.getCount() > 1){


           // for (int month=0; month< schedule.size(); month++) {

                /*CalendarFragment c = mMonthAdapter.mFragmentList.get(month);
                //mMonthAdapter.getItem(month);
                if(!Arrays.equals(c.mListOn, schedule.get(month).onTime) &&
                        !Arrays.equals(c.mListOff, schedule.get(month).offTime)){

                    c.mListOn = schedule.get(month).onTime;
                    c.mListOff = schedule.get(month).offTime;

                    if (c.mListOn != null && c.mListOff != null){
                        if(c.mListOn.length == c.mListOff.length){

                            for (int i = 0; i < c.mListOn.length; i++) {

                                View view = c.getTableLayout().getChildAt(i+1);
                                final TextView on = (TextView) view.findViewById(R.id.tv_on_time);
                                final TextView off = (TextView) view.findViewById(R.id.tv_off_time);

                                try {
                                    on.setText(mDateParser.getTime(c.mListOn[i]));
                                    off.setText(mDateParser.getTime(c.mListOff[i]));

                                }catch (NullPointerException e){
                                    Log.d("TAG", "i=" + i);
                                }

                            }
                        }
                    }
                }*/


            //}

            //DialogHelper.showSuccessMessage(this, getString(R.string.schedule), getString(R.string.schedule_saved));

        }else {

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


            //mCalendarPresenter.generateSchedule(mDayOfYear, on, off);
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
