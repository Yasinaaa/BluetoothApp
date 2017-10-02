package ru.android.bluetooth.calendar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.location.LocationActivity;
import ru.android.bluetooth.main.helper.ScheduleLoading;
import ru.android.bluetooth.one_day.ChangeOneDayScheduleActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.CacheHelper;

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
    @BindView(R.id.fab_menu)
    FloatingActionsMenu fabMenu;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private FilePresenter mFilePresenter;
    private CalendarPresenter mCalendarPresenter;
    private MonthAdapter mMonthAdapter;
    private BluetoothMessage mBluetoothMessage;
    private Calendar mStartDate, mFinishDate;
    private ScheduleLoading scheduleLoading;

    private String mDayToChange = "";
    private String mOnDay = "";
    private String mOffDay = "";
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        start();
    }

    public void fabClicked(View v) {
        switch (v.getId()) {
            case R.id.fab_download_schedule:
                mFilePresenter.setChooseFileDialog();
                break;

            case R.id.fab_save_schedule:

                break;

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
                                    fabMenu.collapse();
                                    fabMenu.collapseImmediately();
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
            mFilePresenter.parseFile(scheduleLoading);
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

        mCoordinatorLayout.getBackground().setAlpha(0);

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mCoordinatorLayout.getBackground().setAlpha(240);
                mCoordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();

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
                mCoordinatorLayout.getBackground().setAlpha(0);
            }
        });

        scheduleLoading = new ScheduleLoading(this, this);
        mMonthAdapter = new MonthAdapter(getSupportFragmentManager());
        mCalendarPresenter.setupViewPager(mTabLayout, mViewPager, mMonthAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public void setScheduleTitle(String title) {
        CacheHelper.setSchedulePath(getApplicationContext(), title);
    }

    @Override
    public void dataCreated(int[] onList, int[] offList) {
        mBluetoothMessage.mStatus = BluetoothCommands.GET_TABLE;
        mBluetoothMessage.writeMessage(this, onList, offList);
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
        TAG = "CalendarActivity";
    }

    @Override
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
    }


    @Override
    public void onLoadingScheduleFinished() {
        HashMap<String[],String[]> schedule = mCalendarPresenter.setTable();
        int month = 0;
        for (Map.Entry<String[],String[]> entry : schedule.entrySet()) {
            String[] key = entry.getKey();
            String[] value = entry.getValue();

            Bundle bundle = new Bundle();
            bundle.putInt(CalendarFragment.MONTH, month);
            bundle.putStringArray(CalendarFragment.ON_LIST, key);
            bundle.putStringArray(CalendarFragment.OFF_LIST, value);

            CalendarFragment calendarFragment = new CalendarFragment();
            calendarFragment.setArguments(bundle);
            mMonthAdapter.addFragment(calendarFragment);
            month++;
        }
    }

    @Override
    public void onItemClick(int month, int day, String text, String on, String off) {
        Calendar clickedDate = mStartDate;
        clickedDate.set(Calendar.MONTH, month);
        clickedDate.set(Calendar.DAY_OF_MONTH, day);

        mId = clickedDate.get(Calendar.DAY_OF_YEAR);
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

        if (requestCode == ChangeOneDayScheduleActivity.REQUEST_CODE){

            int on, off;
            try {
                on = data.getIntExtra(ChangeOneDayScheduleActivity.ON_LOG, 0);
            }catch (RuntimeException e){
                on = 0;
            }
            try {
                off = data.getIntExtra(ChangeOneDayScheduleActivity.OFF_LOG, 0);
            }catch (RuntimeException e){
                off = 0;
            }

            CalendarFragment calendarFragment = mMonthAdapter.getItem(mViewPager.getCurrentItem());
            TableLayout tableLayout = calendarFragment.getTableLayout();
            mCalendarPresenter.saveChanges(mId, on, off, tableLayout);
            //mCalendarPresenter.generateSchedule(mId, on, off);
        }

    }

    @Override
    public void setLonLat(double lat, double lon) {
        mIsScheduleGeneration = false;
        mCalendarPresenter.generateSchedule(mStartDate, mFinishDate, lat, lon, getTimeZone());
        fabMenu.collapseImmediately();
        fabMenu.setSelected(false);
    }

    @Override
    public boolean setScheduleGeneration() {
        return false;
    }
}
