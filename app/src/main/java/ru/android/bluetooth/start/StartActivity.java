package ru.android.bluetooth.start;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.android.bluetooth.R;
import ru.android.bluetooth.utils.ActivityHelper;

/**
 * Created by yasina on 15.09.17.
 */

public class StartActivity extends AppCompatActivity{

    private Activity activity = this;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        ActivityHelper.startActivityAndFinishThis(activity, ChooseDeviceActivity.class);
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
                try {
                    if (mBTAdapter.isEnabled()){
                        ActivityHelper.startActivityAndFinishThis(activity, ChooseDeviceActivity.class);
                    }else {
                        mBTAdapter.enable();
                    }

                }catch (NullPointerException e){
                    // accessLocationPermission();
                }
            }
        }, 100);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

}
