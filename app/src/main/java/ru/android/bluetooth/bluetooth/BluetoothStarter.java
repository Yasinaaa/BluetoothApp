package ru.android.bluetooth.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import butterknife.BindView;
import ru.android.bluetooth.start.ChooseDeviceActivity;
import ru.android.bluetooth.utils.ActivityHelper;

/**
 * Created by yasina on 22.09.17.
 */

public class BluetoothStarter {

    private BroadcastReceiver mReceiver;
    private Activity mActivity;

    public BluetoothStarter(Activity activity) {
        this.mActivity = activity;
    }

    public void openBluetooth(final BluetoothAdapter bluetoothAdapter, final BluetoothView bluetoothView){
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                    switch (bluetoothState) {
                        case BluetoothAdapter.STATE_ON:
                            bluetoothView.nextStepAfterBluetoothOpened();
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            bluetoothAdapter.enable();
                            break;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mActivity.registerReceiver(mReceiver, filter);
    }


    public void destroy(){
        mActivity.unregisterReceiver(mReceiver);
    }

    public interface BluetoothView {
        void nextStepAfterBluetoothOpened();
    }
}
