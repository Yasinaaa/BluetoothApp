package ru.android.autorele.start;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ru.android.autorele.R;
import ru.android.autorele.adapter.DeviceAdapter;
import ru.android.autorele.bluetooth.BluetoothModule;
import ru.android.autorele.main.MainActivity;
import ru.android.autorele.root.RootActivity;
import ru.android.autorele.utils.ActivityHelper;
import ru.android.autorele.utils.BluetoothHelper;
import ru.android.autorele.utils.DialogHelper;


/**
 * Created by itisioslab on 01.08.17.
 */

public class ChooseDeviceActivity
        extends RootActivity
        implements ChooseDeviceView, DeviceAdapter.OnItemClicked {

    @BindView(R.id.rv_devices)
    RecyclerView mRvDevicesList;
    @BindView(R.id.btn_connect)
    Button mBtnConnect;

    private DeviceAdapter mDeviceAdapter;
    private List<String> mDeviceList = new ArrayList<String>();
    private BluetoothModule mBluetoothModule;
    private String mDeviceTitle;
    private Activity mActivity;
    public static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        start();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void init(){

        mActivity = this;
        BluetoothHelper.saveBluetoothOpened(getApplicationContext(), false);

        mDeviceAdapter = new DeviceAdapter(mDeviceList, this);
        mRvDevicesList.setAdapter(mDeviceAdapter);

        mBluetoothModule = BluetoothModule.createBluetoothModule(this, this);
        ActivityHelper.setVisibleLogoIcon(ChooseDeviceActivity.this);

        mRvDevicesList.setItemAnimator(new DefaultItemAnimator());
        mRvDevicesList.setHasFixedSize(true);
        mRvDevicesList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void setClickListeners() {
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDeviceTitle != null) {
                    dialog = DialogHelper.showProgressBar(mActivity, getString(R.string.connect_to_device) + mDeviceTitle);
                    mBluetoothModule.connectDevice(mDeviceTitle);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void setTag() {
        TAG = "ChooseDeviceActivity";
    }

    @Override
    public void addDevice(String text) {
        mDeviceAdapter.add(text);
    }

    @Override
    public void onItemClick(String text) {
        mDeviceTitle = text;
    }

    @Override
    public void goNext(){
        Log.d("t","t");
        ActivityHelper.startActivity(ChooseDeviceActivity.this, MainActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] user = BluetoothHelper.getBluetoothUser(getApplicationContext());
        String newDevice = user[1] + "\n" + user[0];

        if (!newDevice.equals(mDeviceTitle) && mDeviceTitle != null){
            mDeviceTitle = user[1] + "\n" + user[0];
            mDeviceAdapter.getItem(user[0], user[1]);
            mBluetoothModule.setView(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mBluetoothModule != null) {
            mBluetoothModule.unregister();
        }
    }

    @Override
    public void error(String message){
        dialog.cancel();
        Log.d(TAG, message);
        DialogHelper.showErrorMessage(mActivity, getString(R.string.device_is_not_on_net));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int gr : grantResults) {
                        if (gr != PackageManager.PERMISSION_GRANTED) {
                            mBluetoothModule.setEnableBluetooth();
                            return;
                        }
                    }
                }
                break;
            default:
                return;
        }
    }
}
