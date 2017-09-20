package ru.android.bluetooth.view;

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
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.one_day.ChangeOneDayScheduleActivity;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.schedule.ScheduleGeneratorActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.CacheHelper;

/**
 * Created by itisioslab on 03.08.17.
 */

public class CalendarActivity extends RootActivity implements CalendarModule.View {

    //@BindView(R.id.calendar_view_schedule)
    //CalendarView mCalendarViewSchedule;

    @BindView(R.id.tableLayout)
    TableLayout mTableLayout;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;

    private CalendarPresenter mCalendarPresenter;
    private BluetoothMessage mBluetoothMessage;

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
    AlertDialog alertDialog, alertDialog2;
    public void fabClicked(View v){
       switch (v.getId()){
           case R.id.fab_generate_shedule_hand_one_day:
               //ActivityHelper.startActivity(CalendarActivity.this, ChangeOneDayScheduleActivity.class);
               AlertDialog.Builder dialog2 = new AlertDialog.Builder(getApplicationContext())
                       .setTitle("Автореле")
                       .setMessage("Данная функция не доступна в данной версии")
                       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               // Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                               alertDialog2.cancel();
                           }
                       });
               alertDialog = dialog2.create();
               dialog2.show();
               break;
           case R.id.fab_load_schedule:
               //ActivityHelper.startActivity(CalendarActivity.this, GenerateHandActivity.class);
               requestWritePermission();
               break;

           case R.id.fab_generate_schedule_sunrise_set:
               //ActivityHelper.startActivity(CalendarActivity.this, ScheduleGeneratorActivity.class);
               Calendar finishDate = Calendar.getInstance();
               finishDate.add(Calendar.YEAR, 1);
               String[] result = CacheHelper.getCoordinatesAndTimezone(getApplicationContext());
               if(result != null){
                   mCalendarPresenter.generateSchedule(Calendar.getInstance(), finishDate,
                           Double.parseDouble(result[0]),  Double.parseDouble(result[1]),
                           Integer.parseInt(result[2]));
               }else {

                   AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                           .setTitle("Настройки")
                           .setMessage("Укажите свое местоположение в Настройках")
                           .setNegativeButton("Ок", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                   alertDialog.dismiss();
                               }
                           });
                   alertDialog = dialog.create();
                   dialog.show();
               }

               //mScheduleBluetoothReader.readSchedule(Calendar.getInstance(), finishDate);
               /*mStatus = BluetoothCommands.SET_DATA;
               mAlertDialog = ActivityHelper.showProgressBar(mActivity, getString(R.string.generate_schedule));
               mBluetoothMessage.writeMessage(onList, offList);*/
               break;
       }
    }

    private static final int REQUEST_READ_PERMISSION = 785;
    private static final int REQUEST_WRITE_PERMISSION = 786;

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
        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();

        mCalendarPresenter = new CalendarPresenter(this, mBluetoothMessage, this);
        mCalendarPresenter.getSchedule();
        //mCalendarPresenter.setTable(mTableLayout);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
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

                /*Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month-1);
                calendar.set(Calendar.DAY_OF_MONTH, day);*/
                mCalendarPresenter.searchDay(date, mTableLayout, mNestedScrollView);


               // mCalendarViewSchedule.setDate(milliTime, true, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String date) {
                //String date = "22/3/2014";

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
}
