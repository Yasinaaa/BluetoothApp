package ru.android.bluetooth.start;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.adapter.DeviceAdapter;
import ru.android.bluetooth.bluetooth.BluetoothModule;
import ru.android.bluetooth.main.MainActivity;
import ru.android.bluetooth.utils.ActivityHelper;

/**
 * Created by itisioslab on 01.08.17.
 */

public class ChooseDeviceActivity extends AppCompatActivity implements ChooseDeviceView, DeviceAdapter.OnItemClicked {

    @BindView(R.id.rv_devices)
    RecyclerView mRvDevicesList;
    @BindView(R.id.btn_connect)
    Button mBtnConnect;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;


    private DeviceAdapter mDeviceAdapter;
    private List<String> mDeviceList = new ArrayList<String>();
    private BluetoothModule mBluetoothModule;
    private String mDeviceTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       checkBluetoothUser();

       getSupportActionBar().hide();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                frameLayout.setVisibility(View.INVISIBLE);
                getSupportActionBar().show();

                ActivityHelper.setVisibleIcon(ChooseDeviceActivity.this);
                scrollView.setVisibility(View.VISIBLE);
            }
        }, 1000);


    }

    private int sum(String chars){
        int sum = 0;
        for (int i=0; i<chars.length();i++){
            sum += chars.charAt(i);
        }
        Log.d("dd", "sum=" + sum);
        return sum;
    }

    private void checkBluetoothUser(){
        /*if(!BluetoothHelper.isFirstLaunch(getApplicationContext())){
            goNext();
        }else {
            setContentView(R.layout.activity_choose_device);
            ButterKnife.bind(this);
            mBluetoothModule = BluetoothModule.createBluetoohModule(this, this);
            init();
        }*/
        setContentView(R.layout.activity_choose_device);
        ButterKnife.bind(this);
        mBluetoothModule = BluetoothModule.createBluetoohModule(this, this);

        init();
    }

    private void init(){
        mRvDevicesList.setItemAnimator(new DefaultItemAnimator());
        mRvDevicesList.setHasFixedSize(true);
        mRvDevicesList.setLayoutManager(new LinearLayoutManager(this));
        setPasswordDialog();
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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setPasswordDialog(){
        final Activity activity = this;
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDeviceTitle != null)
                    mBluetoothModule.connectDevice(mDeviceTitle, ChooseDeviceActivity.this);
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothModule.unregister();
    }

    @Override
    public void onItemClick(String text) {
        mDeviceTitle = text;
    }

    @Override
    public void goNext(){
        ActivityHelper.startActivity(ChooseDeviceActivity.this, MainActivity.class);
    }
}
