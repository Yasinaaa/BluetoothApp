package ru.android.autorele.start;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.android.autorele.R;
import ru.android.autorele.bluetooth.BluetoothStarter;
import ru.android.autorele.utils.ActivityHelper;

/**
 * Created by yasina on 15.09.17.
 */

public class StartActivity extends AppCompatActivity implements BluetoothStarter.BluetoothView{

    private Activity mActivity = this;
    private BluetoothAdapter mBTAdapter;
    private BluetoothStarter mBluetoothStarter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide();

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothStarter = new BluetoothStarter(mActivity);
        mBluetoothStarter.openBluetooth(mBTAdapter, this);

        try {
            if (mBTAdapter.isEnabled()){
                nextStepAfterBluetoothOpened();
            }else {
                mBTAdapter.enable();
            }
        }catch (NullPointerException e){
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothStarter.destroy();
    }

    @Override
    public void nextStepAfterBluetoothOpened() {
        ActivityHelper.startActivityAndFinishThis(mActivity, ChooseDeviceActivity.class);
    }
}
