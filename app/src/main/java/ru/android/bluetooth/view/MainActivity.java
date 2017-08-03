package ru.android.bluetooth.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.utils.ActivityHelper;

/**
 * Created by itisioslab on 01.08.17.
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_device_title)
    TextView mTvDeviceTitle;
    @BindView(R.id.switch_on_off_device)
    Switch mSwitchOnOffDevice;
    @BindView(R.id.cv_on_off_info)
    CardView mCvOnOffInfo;
    @BindView(R.id.tv_day)
    TextView mTvDay;
    @BindView(R.id.tv_on)
    TextView mTvOn;
    @BindView(R.id.tv_off)
    TextView mTvOff;
    @BindView(R.id.cv_controller_functions)
    CardView mCvControllerFunctions;
    @BindView(R.id.tv_reset)
    TextView mTvReset;
    @BindView(R.id.btn_reset_controller)
    Button mBtnResetController;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.ib_sync_status)
    ImageButton mIbSyncStatus;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.ib_sync_version)
    ImageButton mIbSyncVersion;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.btn_set_date)
    Button mBtnSetDate;
    @BindView(R.id.cv_schedule)
    CardView mCvSchedule;
    @BindView(R.id.et_schedule)
    EditText mEtSchedule;
    @BindView(R.id.btn_remove_schedule)
    Button mBtnRemoveSchedule;
    @BindView(R.id.tv_edit_schedule)
    TextView mTvEditSchedule;
    @BindView(R.id.tv_generate_schedule)
    TextView mTvGenerateschedule;
    @BindView(R.id.tv_new_schedule)
    TextView mTvNewSchedule;
    @BindView(R.id.tb_mode_device)
    ToggleButton mTbSwitchModeDevice;
    @BindView(R.id.rl)
    RelativeLayout mRl;

    private RelativeLayout.LayoutParams mRlLayoutParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init(){

        mRlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        setMode();

        mTbSwitchModeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setMode();
            }
        });

        mTvEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startActivity(MainActivity.this, CalendarActivity.class);
            }
        });
    }

    private void setMode(){
        if(mTbSwitchModeDevice.getText().equals(getResources().getString(R.string.manual_mode))){
            setModeVisiblity(View.INVISIBLE);
            mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_controller_functions);
        }else {
            setModeVisiblity(View.VISIBLE);
            mRlLayoutParams.addRule(RelativeLayout.BELOW, R.id.cv_on_off_info);
        }
        mRlLayoutParams.setMargins(0,10,0,0);
        mCvSchedule.setLayoutParams(mRlLayoutParams);
    }

    private void setModeVisiblity(int visiblity){
        mCvControllerFunctions.setVisibility(visiblity == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        mCvOnOffInfo.setVisibility(visiblity);

        if(visiblity == View.INVISIBLE){
            setScheduleModeVisiblity(View.GONE);
        }else {
            setScheduleModeVisiblity(View.VISIBLE);
        }

    }

    private void setScheduleModeVisiblity(int visiblity){
        mEtSchedule.setVisibility(visiblity);
        mBtnRemoveSchedule.setVisibility(visiblity);
        mTvEditSchedule.setVisibility(visiblity);
    }

    // arguing for it
    private int setOppositeVisiblity(int visiblity){
        return visiblity == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
