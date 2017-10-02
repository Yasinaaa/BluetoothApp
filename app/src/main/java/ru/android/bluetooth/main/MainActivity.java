package ru.android.bluetooth.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothCommands;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.main.helper.ScheduleLoading;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.settings.SettingsActivity;
import ru.android.bluetooth.start.ChooseDeviceActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.CacheHelper;
import ru.android.bluetooth.calendar.CalendarActivity;
import ru.android.bluetooth.utils.DialogHelper;


/**
 * Created by itisioslab on 01.08.17.
 */

public class MainActivity extends RootActivity implements MainModule.View,
        BluetoothMessage.BluetoothMessageListener{

    @BindView(R.id.cv_controller_functions)
    CardView mCvControllerFunctions;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.btn_sync_status)
    Button mIbSyncStatus;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.btn_sync_version)
    Button mIbSyncVersion;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.btn_set_date)
    Button mBtnSetDate;
    @BindView(R.id.cv_schedule)
    CardView mCvSchedule;
    @BindView(R.id.tv_edit_schedule)
    TextView mTvEditSchedule;
    @BindView(R.id.tv_generate_schedule)
    TextView mTvGenerateSchedule;
    @BindView(R.id.rl)
    RelativeLayout mRl;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.btn_set_time)
    Button mBtnSetTime;
    @BindView(R.id.et_schedule_name)
    EditText mEtScheduleName;
    @BindView(R.id.btn_edit_schedule)
    Button mBtnEditSchedule;

    private BluetoothMessage mBluetoothMessage;
    private MainPresenter mMainPresenter;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
    }

    @Override
    public void init(){
        // close previous Dialog
        try {
            ChooseDeviceActivity.dialog.cancel();
        }catch (java.lang.NullPointerException e){

        }

        mActivity = this;
        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        mMainPresenter = new MainPresenter(mActivity, mBluetoothMessage);
        mMainPresenter.setScheduleFilePath(mEtScheduleName);
        mMainPresenter.sendStatusMessage();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void setTag() {
        TAG = "MainActivity";
    }

    @Override
    public void setStatus(String status) {
        mTvStatus.setText(status);
    }

    @Override
    public void onResponse(String answer) {
        //Log.d(TAG, mBluetoothMessage.mStatus + " " + answer);

        if(mBluetoothMessage.mStatus!= null) {
            switch (mBluetoothMessage.mStatus) {

                case BluetoothCommands.STATUS:

                    mMainPresenter.parseResponse(answer, new MainPresenter.ResponseParseView() {
                        @Override
                        public void nextJob(String text) {
                            mTvStatus.setText(text);
                            mMainPresenter.parseStatus(text, mTvDate, mTvTime);
                        }
                    });
                    break;

                case BluetoothCommands.VERSION:

                    mMainPresenter.parseResponse(answer, new MainPresenter.ResponseParseView() {
                        @Override
                        public void nextJob(String text) {
                            mTvVersion.setText(text);
                        }
                    });
                    break;

                case BluetoothCommands.GET_TIME:

                    mMainPresenter.parseResponse(answer, new MainPresenter.ResponseParseView() {
                        @Override
                        public void nextJob(String text) {
                            mMainPresenter.getTimeResponse(text, mTvDate, mTvTime);
                        }
                    });

                    break;

                case BluetoothCommands.SET_DATE:
                    mMainPresenter.setTimeDateResponse(answer, mTvDate);
                    break;

                case BluetoothCommands.SET_TIME:
                    mMainPresenter.setTimeDateResponse(answer, mTvTime);
                    break;
            }
        }
    }

    @Override
    public void setClickListeners(){

        mMainPresenter.setDateTimeClickListeners(mBtnSetTime, mBtnSetDate);

        mBtnEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startActivity(MainActivity.this, CalendarActivity.class);
            }
        });

        mIbSyncStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPresenter.sendStatusMessage();
            }
        });

        mIbSyncVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPresenter.sendVersionMessage();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_settings);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ActivityHelper.startActivity(mActivity, SettingsActivity.class);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.setScheduleFilePath(mEtScheduleName);
    }
}
