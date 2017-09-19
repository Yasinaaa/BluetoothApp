package ru.android.bluetooth.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.hand_generation.GenerateHandActivity;
import ru.android.bluetooth.one_day.ChangeOneDayScheduleActivity;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.schedule.ScheduleGeneratorActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

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

    public void fabClicked(View v){
       switch (v.getId()){
           case R.id.fab_generate_shedule_hand_one_day:
               ActivityHelper.startActivity(CalendarActivity.this, ChangeOneDayScheduleActivity.class);
               break;
           case R.id.fab_load_schedule:
               //ActivityHelper.startActivity(CalendarActivity.this, GenerateHandActivity.class);

               break;
           case R.id.fab_generate_schedule_sunrise_set:
               ActivityHelper.startActivity(CalendarActivity.this, ScheduleGeneratorActivity.class);
               break;
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0x11) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // save file
            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
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
