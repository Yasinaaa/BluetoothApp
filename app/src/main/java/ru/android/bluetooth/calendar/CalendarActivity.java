package ru.android.bluetooth.calendar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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
import butterknife.BindView;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.location.LocationActivity;
import ru.android.bluetooth.one_day.ChangeOneDayScheduleActivity;

/**
 * Created by itisioslab on 03.08.17.
 */

public class CalendarActivity extends LocationActivity
        implements CalendarModule.View, CalendarModule.OnItemClicked {

    private static final int REQUEST_WRITE_PERMISSION = 786;

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
    private Calendar mStartDate, mFinishDate;

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

        mCalendarPresenter = new CalendarPresenter(this, mBluetoothMessage, this, this);
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
        this.mId = id;
        mDayToChange = text;
        mOnDay = on;
        mOffDay = off;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

            mCalendarPresenter.generateSchedule(mId, on, off);
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
