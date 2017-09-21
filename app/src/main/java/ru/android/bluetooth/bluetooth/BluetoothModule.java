package ru.android.bluetooth.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ru.android.bluetooth.Manifest;
import ru.android.bluetooth.start.ChooseDeviceActivity;
import ru.android.bluetooth.start.ChooseDeviceModule;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;

/**
 * Created by yasina on 07.08.17.
 */

public class BluetoothModule {


    private ConnectedThread mConnectedThread;
    private BluetoothSocket mBTSocket = null;

    private UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Context mContext;
    private Activity mActivity;
    private ChooseDeviceModule.ChooseDeviceView mView;
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


    private BluetoothModule(Activity mActivity, ChooseDeviceModule.ChooseDeviceView view) {
        this.mActivity = mActivity;
        this.mContext = mActivity.getBaseContext();
        this.mView = view;
        connect();
    }

    public static BluetoothModule createBluetoohModule(Activity activity, ChooseDeviceModule.ChooseDeviceView view){
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
        try {
            mBTAdapter.enable();
            discover();
        }catch (NullPointerException e){
            /*Intent enableBluetoothIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBluetoothIntent, 0);*/
            accessLocationPermission();
        }
        //mBTAdapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.HEADSET);
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

        //Toast.makeText(mContext, "Discovery started", Toast.LENGTH_SHORT).show();
        mBTAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        mActivity.registerReceiver(blReceiver, filter);

        listPairedDevices();
    }

    public void unregister(){
        try {
            mActivity.unregisterReceiver(blReceiver);
        }catch (java.lang.IllegalArgumentException e){

        }
        //mBTAdapter.disable();
    }

    public void register(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        mActivity.registerReceiver(blReceiver, filter);
    }

    /*public void unregister(Activity a){
        //try {
            a.unregisterReceiver(blReceiver);
       // }catch (java.lang.IllegalArgumentException e){

        //}
        //mBTAdapter.disable();
    }*/

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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        connectD();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        mBTAdapter.enable();
                        break;
                }
            }
        }
    };
    private boolean isFirstOpen = true;
    String address, name;
    ChooseDeviceModule.ChooseDeviceView mChooseDeviceView;

    public void connectDevice(String info, ChooseDeviceModule.ChooseDeviceView chooseDeviceView){

            mChooseDeviceView = chooseDeviceView;
            if(!mBTAdapter.isEnabled()) {
                //Toast.makeText(mContext, "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            address = info.substring(info.length() - 17);
            name = info.substring(0,info.length() - 18);

            new Thread()
            {
                public void run() {

                    String[] answer = BluetoothHelper.getBluetoothUser(mContext);
                    //if(answer[0] == address
                    Log.d("TAG", String.valueOf(!BluetoothHelper.isOpen(mContext)) + String.valueOf(answer[0] != address));
                    //!BluetoothHelper.isOpen(mContext) || !answer[0].equals(address)

                    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    mActivity.registerReceiver(mReceiver, filter);
                    mBTAdapter.disable();

                }
            }.start();
    }

    private void connectD(){
        boolean fail = false;
        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

        try {
            mBTSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            fail = true;
            //Toast.makeText(mContext, "Socket creation failed", Toast.LENGTH_SHORT).show();
        }
        try {

            if (!mBTSocket.isConnected()) {
                mBTSocket.connect();
            }

        } catch (final IOException e) {
            Log.e("TAG", e.getMessage());
            try {
                fail = true;
                mBTSocket.close();
                mChooseDeviceView.error(e.getMessage());


            } catch (IOException ex) {

            }
                        /*fail = true;
                        mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                              //
                                //  mActivity.unregisterReceiver(blReceiver);
                                chooseDeviceView.error(e.getMessage());

                            }
                        });
                        try {
                            mBTSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }*/

        }
        if (fail == false) {
            //mActivity.unregisterReceiver(blReceiver);
            mConnectedThread = ConnectedThread.createConnectedThread(mBTSocket, mHandler);
            mConnectedThread.start();
            mBluetoothMessage.setConnectedThread(mConnectedThread);

            mHandler.obtainMessage(BluetoothCommands.CONNECTING_STATUS, 1, -1, name)
                    .sendToTarget();

            isFirstOpen = false;
            BluetoothHelper.saveBluetoothOpened(mContext, true);
            mChooseDeviceView.goNext();
            BluetoothHelper.saveBluetoothUser(mContext, address, name);
                       /* try {
                            mBTSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/

        }
        mActivity.unregisterReceiver(mReceiver);

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //BTMODULEUUID = UUID.nameUUIDFromBytes("jj".getBytes());
        Log.d("TAG", "b=" + BTMODULEUUID);
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }



    public static final String baseBluetoothUuidPostfix = "0000-1000-8000-00805F9B34FB";

    public static UUID uuidFromShortCode16(String shortCode16) {
        return UUID.fromString("0000" + shortCode16 + "-" + baseBluetoothUuidPostfix);
    }

    public static UUID uuidFromShortCode32(String shortCode32) {
        return UUID.fromString(shortCode32 + "-" + baseBluetoothUuidPostfix);
    }
}
