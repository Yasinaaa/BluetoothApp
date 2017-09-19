package ru.android.bluetooth.start;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.adapter.DeviceAdapter;
import ru.android.bluetooth.bluetooth.BluetoothModule;
import ru.android.bluetooth.main.MainActivity;
import ru.android.bluetooth.root.RootActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;

/**
 * Created by itisioslab on 01.08.17.
 */

public class ChooseDeviceActivity extends RootActivity implements ChooseDeviceModule.ChooseDeviceView,
        DeviceAdapter.OnItemClicked {

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

    public static AlertDialog dialog;
    private boolean isFirstOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_choose_device);
        start();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //test();
    }

    /*public void test(){
        ArrayList<String> testItems = new ArrayList<String>();
        testItems.add("is=352,1,12");
        testItems.add("352,1,,11, 12");
        //testItems.add("   ");
        testItems.add("is=353,13,14");
        testItems.add("is=354,5,6");
        testItems.add("is=355,17,18");
        testItems.add("is=356,19,21");
        // is=353,1,1
        for (String item: testItems){
            if (item.matches("(.*)=\\d+,\\d+,\\d+")) {
                try {
                    Log.d("f", "item1= " + item.substring(item.indexOf("=") + 1, item.indexOf(",")));
                    Log.d("f", "item2= " + item.substring(item.indexOf(",") + 1, item.lastIndexOf(",")));
                    Log.d("f", "item3= " + item.substring(item.lastIndexOf(",") + 1, item.length()));
                } catch (java.lang.Exception e) {

                }
            }
        }


    }*/

    @Override
    public void init(){

        BluetoothHelper.saveBluetoothOpened(getApplicationContext(), false);
        mBluetoothModule = BluetoothModule.createBluetoohModule(this, this);
        mBluetoothModule.register();
        ActivityHelper.setVisibleLogoIcon(ChooseDeviceActivity.this);

        mRvDevicesList.setItemAnimator(new DefaultItemAnimator());
        mRvDevicesList.setHasFixedSize(true);
        mRvDevicesList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void setClickListeners() {
        final Activity activity = this;
        final ChooseDeviceModule.ChooseDeviceView viewC = this;
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFirstOpen){
                   // mBluetoothModule
                }
                if(mDeviceTitle != null) {
                    dialog = ActivityHelper.showProgressBar(activity);
                    mBluetoothModule.connectDevice(mDeviceTitle, ChooseDeviceActivity.this);
                    isFirstOpen = false;
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

    /*public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        //dialog.cancel();
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
        Activity a = this;
       // mBluetoothModule.unregister(a);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(mBluetoothModule != null)
       //     mBluetoothModule.unregister();

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
        AlertDialog.Builder dialogB = new AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage("Устройство не находится в сети")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialogB.show();
       // mBluetoothModule.unregister();
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
