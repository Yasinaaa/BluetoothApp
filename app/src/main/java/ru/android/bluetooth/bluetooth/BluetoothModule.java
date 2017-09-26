package ru.android.bluetooth.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ru.android.bluetooth.Manifest;
import ru.android.bluetooth.start.ChooseDeviceView;
import ru.android.bluetooth.utils.BluetoothHelper;

/**
 * Created by yasina on 07.08.17.
 */

public class BluetoothModule implements BluetoothStarter.BluetoothView{

    private UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;
    private BluetoothSocket mBTSocket = null;
    private static Context mContext;
    private static Activity mActivity;
    private static ChooseDeviceView mView;
    private static BluetoothModule mBluetoothModule = null;
    private BluetoothAdapter mBTAdapter;
    private BluetoothMessage mBluetoothMessage;
    private Handler mHandler;
    private Set<BluetoothDevice> mPairedDevices;
    private BluetoothStarter mBluetoothStarter;
    private String mAddress, mName;

    private BluetoothModule(Activity mActivity, ChooseDeviceView view) {
        this.mActivity = mActivity;
        this.mContext = mActivity.getBaseContext();
        this.mView = view;
        connect();
    }

    public static BluetoothModule createBluetoothModule(Activity activity, ChooseDeviceView view){
        if (mBluetoothModule == null){
            mBluetoothModule = new BluetoothModule(activity, view);
        }
        mActivity = activity;
        mContext = mActivity.getBaseContext();
        mView = view;
        return mBluetoothModule;
    }

    public void connect(){
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        setHandler();
        try {
            mBTAdapter.enable();
            discoverDevices();
        }catch (NullPointerException e){
            accessLocationPermission();
        }
    }

    private void accessLocationPermission() {
        int accessCoarseLocation = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation   = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listRequestPermission = new ArrayList<String>();

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
            ActivityCompat.requestPermissions(mActivity, strRequestPermission, 1);
        }
    }

    public void setEnableBluetooth(){
        mBTAdapter.enable();
    }

    private void setHandler(){
        this.mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        this.mHandler = mBluetoothMessage.getHandler();
    }

    public void discoverDevices(){
        mBTAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        mActivity.registerReceiver(blReceiver, filter);

        getPairedDevices();
    }

    public void unregister(){
        try {
            mActivity.unregisterReceiver(blReceiver);
        }catch (java.lang.IllegalArgumentException e){

        }
    }
    
    private void getPairedDevices(){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            for (BluetoothDevice device : mPairedDevices) {
                mView.addDevice(device.getName() + "\n" + device.getAddress());
            }
        }else{
        }
    }

    public final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            mView.addDevice(device.getName() + "\n" + device.getAddress());
        }
    };

    public void connectDevice(String info){

        if (mView != null) {
            if (!mBTAdapter.isEnabled()) {
                return;
            }

            if (info != null){
                mAddress = info.substring(info.length() - 17);
                mName = info.substring(0, info.length() - 18);
            }

            final BluetoothStarter.BluetoothView bluetoothView = this;

            new Thread() {
                public void run() {
                    mBluetoothStarter = new BluetoothStarter(mActivity);
                    mBluetoothStarter.openBluetooth(mBTAdapter, bluetoothView);
                    mBTAdapter.disable();
                }
            }.start();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void nextStepAfterBluetoothOpened() {
        boolean fail = false;
        BluetoothDevice device = mBTAdapter.getRemoteDevice(mAddress);

        try {
            mBTSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            fail = true;
        }

        try {
            if (!mBTSocket.isConnected()) {
                mBTSocket.connect();
            }

        } catch (final IOException e) {

            try {
                fail = true;
                mBTSocket.close();
                mView.error(e.getMessage());

            } catch (IOException ex) {

            }
        }
        if (!fail) {

            mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
            mConnectedThread.start();
            mBluetoothMessage.setConnectedThread(mConnectedThread);

            mHandler.obtainMessage(BluetoothCommands.CONNECTING_STATUS, 1, -1, mName)
                    .sendToTarget();

            BluetoothHelper.saveBluetoothOpened(mContext, true);
            mView.goNext();
            BluetoothHelper.saveBluetoothUser(mContext, mAddress, mName);
        }
        mBluetoothStarter.destroy();

    }
}
