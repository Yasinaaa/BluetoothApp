package ru.android.autorele.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import ru.android.autorele.R;
import ru.android.autorele.bluetooth.BluetoothCommands;
import ru.android.autorele.bluetooth.BluetoothMessage;
import ru.android.autorele.root.RootActivity;
import ru.android.autorele.settings.SettingsActivity;
import ru.android.autorele.start.ChooseDeviceActivity;
import ru.android.autorele.utils.ActivityHelper;
import ru.android.autorele.calendar.view.CalendarActivity;


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
    private boolean isFirstOpen = true;

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

        mRl.setVisibility(View.GONE);
        mActivity = this;
        mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);


       /* mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        mBluetoothMessage.setBluetoothMessageListener(this);
        mBluetoothMessage.mStatus = BluetoothCommands.STATUS;
        mBluetoothMessage.writeMessage(mActivity, BluetoothCommands.STATUS);*/

        mMainPresenter = new MainPresenter(mActivity, mBluetoothMessage);
        mMainPresenter.setScheduleFilePath(mEtScheduleName);
        mMainPresenter.callDialog();
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
        Log.d(TAG, mBluetoothMessage.mStatus + " " + answer);

        if(mBluetoothMessage.mStatus!= null) {
            switch (mBluetoothMessage.mStatus) {

                case BluetoothCommands.STATUS:


                    mMainPresenter.parseResponse(isFirstOpen, answer, new MainPresenter.ResponseParseView() {
                        @Override
                        public void nextJob(String text) {
                            mIbSyncStatus.setEnabled(true);
                            String[] parse = text.split("\n");
                            mTvStatus.setText("");
                            for(String s: parse){

                                if (s.contains("soft")){
                                    s = s.replaceAll("N", "");
                                }
                                if(s.contains("Manual")){
                                    if (s.contains("On")){
                                        s = "Manual On";
                                    }else if(s.contains("Off")){
                                        s = "Manual Off";
                                    }
                                }
                                mTvStatus.setText(mTvStatus.getText().toString() + s + "\n");
                            }

                           // mTvStatus.setText(text);
                            mMainPresenter.parseStatus(text, mTvDate, mTvTime);
                            if (isFirstOpen){
                                mMainPresenter.sendVersionMessage();
                            }
                        }
                    });
                    break;

                case BluetoothCommands.VERSION:

                    mMainPresenter.parseResponse2(answer, new MainPresenter.ResponseParseView() {
                        @Override
                        public void nextJob(String text) {
                            mIbSyncVersion.setEnabled(true);
                            mTvVersion.setText(text);
                            if (isFirstOpen){
                                mRl.setVisibility(View.VISIBLE);
                                isFirstOpen = false;
                            }
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
                mMainPresenter.callDialog();
                mIbSyncStatus.setEnabled(false);
                mMainPresenter.sendStatusMessage();
            }
        });

        mIbSyncVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPresenter.callDialog();
                mIbSyncVersion.setEnabled(false);
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
        mBluetoothMessage.setBluetoothMessageListener(this);

    }
}
