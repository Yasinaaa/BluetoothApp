package ru.android.bluetooth.bluetooth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import ru.android.bluetooth.Manifest;
import ru.android.bluetooth.start.ChooseDeviceView;
import ru.android.bluetooth.utils.BluetoothHelper;

import static ru.android.bluetooth.bluetooth.BluetoothCommands.REQUEST_ENABLE_BT;

/**
 * Created by yasina on 07.08.17.
 */

public class BluetoothModule {


    private ConnectedThread mConnectedThread;
    private BluetoothSocket mBTSocket = null;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Context mContext;
    private Activity mActivity;
    private ChooseDeviceView mView;
    private static BluetoothModule bluetoothModule = null;
    private BluetoothAdapter mBTAdapter;
    private BluetoothMessage mBluetoothMessage;
    private Handler mHandler;
    private Set<BluetoothDevice> mPairedDevices;

    private BluetoothHeadset mBluetoothHeadset;

    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = (BluetoothHeadset) proxy;
            }


            mBTAdapter.closeProfileProxy(0,mBluetoothHeadset);
        }
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = null;
            }
        }
    };


    private BluetoothModule(Activity mActivity, ChooseDeviceView view) {
        this.mActivity = mActivity;
        this.mContext = mActivity.getBaseContext();
        this.mView = view;
        connect();
    }

    public static BluetoothModule createBluetoohModule(Activity activity, ChooseDeviceView view){
        if (bluetoothModule == null){
            bluetoothModule = new BluetoothModule(activity, view);
        }
        return bluetoothModule;
    }


    public void onDestroy(){
        mBTAdapter.disable();
        mBTAdapter.cancelDiscovery();
    }

    public ConnectedThread getConnectedThread() {
        return mConnectedThread;
    }

    public static BluetoothModule createBluetoohModule(){
        return bluetoothModule;
    }

    private void checkPermission(){
        /*if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }*/
       /* if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("External storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }*/

        //}
      //  ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    public void connect(){
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        //checkPermission();
        setHandler();
        //bluetoothOn();
        mBTAdapter.enable();
        try {
            while(!mBTAdapter.isEnabled()) {
                synchronized (this) {
                    wait(1000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        discover();
        //mBTAdapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.HEADSET);


    }

    private void setHandler(){
        this.mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        this.mHandler = mBluetoothMessage.getHandler();
    }

    public void discover(){
        /*if(mBTAdapter.isDiscovering()){
            Toast.makeText(mContext,"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTAdapter.startDiscovery();
                Toast.makeText(mContext, "Discovery started", Toast.LENGTH_SHORT).show();
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                //filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
                //filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                mActivity.registerReceiver(blReceiver, filter);
            }
            else{
                Toast.makeText(mContext, "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }*/
        //mBTAdapter.enable();
        mBTAdapter.startDiscovery();
        //Toast.makeText(mContext, "Discovery started", Toast.LENGTH_SHORT).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        mActivity.registerReceiver(blReceiver, filter);
        listPairedDevices();
    }

    public void unregister(){
        mActivity.unregisterReceiver(blReceiver);
        mBTAdapter.disable();
    }

    private void listPairedDevices(){
        mPairedDevices = mBTAdapter.getBondedDevices();
        /*if(mBTAdapter.isEnabled()) {
            for (BluetoothDevice device : mPairedDevices){
                mView.addDevice(device.getName() + "\n" + device.getAddress());
            }
            Toast.makeText(mActivity, "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(mActivity, "Bluetooth not on", Toast.LENGTH_SHORT).show();*/
       // mBTAdapter.enable();
        if(mBTAdapter.isEnabled()) {
            for (BluetoothDevice device : mPairedDevices) {
                mView.addDevice(device.getName() + "\n" + device.getAddress());
            }
        }else{
           // Toast.makeText(mActivity, "Bluetooth not on", Toast.LENGTH_SHORT).show();
        }


    }

    public final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            /*if(device != null){
                Log.d("f",device.getName());
                mView.addDevice(device.getName() + "\n" + device.getAddress());
            }*/
            mView.addDevice(device.getName() + "\n" + device.getAddress());
            /*if(BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                mView.addDevice(device.getName() + "\n" + device.getAddress());
            }*/
        }
    };

    public void connectDevice(String info, final ChooseDeviceView chooseDeviceView){

            if(!mBTAdapter.isEnabled()) {
                //Toast.makeText(mContext, "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        //Toast.makeText(mContext, "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        mBTSocket.connect();
                    } catch (final IOException e) {
                        /*try {
                            fail = true;
                            mBTSocket.close();

                            chooseDeviceView.error(e.getMessage());

                        } catch (IOException ex) {

                        }*/
                        fail = true;
                        /*mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                chooseDeviceView.error(e.getMessage());
                            }
                        });*/
                        //
                    }
                    if(fail == false) {
                        mConnectedThread = ConnectedThread.createConnectedThread(mBTSocket, mHandler);
                        mConnectedThread.start();
                        mBluetoothMessage.setConnectedThread(mConnectedThread);

                        mHandler.obtainMessage(BluetoothCommands.CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();

                        chooseDeviceView.goNext();
                        BluetoothHelper.saveBluetoothUser(mContext, address, name);

                    }
                }
            }.start();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

}
