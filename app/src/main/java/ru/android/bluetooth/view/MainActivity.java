package ru.android.bluetooth.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;

/**
 * Created by itisioslab on 01.08.17.
 */

public class MainActivity extends AppCompatActivity {

    private final String ON = "on";
    private final String OFF = "off";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        mTbSwitchModeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTbSwitchModeDevice.getText().equals(ON)){

                }else {

                }
            }
        });
    }
}
