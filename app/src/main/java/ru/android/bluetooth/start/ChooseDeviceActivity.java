package ru.android.bluetooth.start;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ru.android.bluetooth.R;
import ru.android.bluetooth.adapter.DeviceAdapter;
import ru.android.bluetooth.bluetooth.BluetoothModule;
import ru.android.bluetooth.main.MainActivity;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;
import ru.android.bluetooth.utils.DialogHelper;

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
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private DeviceAdapter mDeviceAdapter;
    private List<String> mDeviceList = new ArrayList<String>();
    private BluetoothModule mBluetoothModule;
    private String mDeviceTitle;
    private Activity mActivity;
    public static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_choose_device);
        start();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void init(){

        mActivity = this;
        BluetoothHelper.saveBluetoothOpened(getApplicationContext(), false);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    mDeviceAdapter.getFilter().filter(newText);
                }else {
                    mDeviceAdapter.setUsualList();

                }

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.cancelLongPress();
                return false;
            }
        });
        return true;
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
        if (mDeviceAdapter == null) {
            mDeviceAdapter = new DeviceAdapter(mDeviceList, this);
            mRvDevicesList.setAdapter(mDeviceAdapter);
        }
        mDeviceAdapter.add(text);
    }

    @Override
    public void onItemClick(String text) {
        mDeviceTitle = text;
    }

    @Override
    public void goNext(){
        ActivityHelper.startActivity(ChooseDeviceActivity.this, MainActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
