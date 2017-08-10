package ru.android.bluetooth.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;
import ru.android.bluetooth.adapter.DeviceAdapter;
import ru.android.bluetooth.bluetooth.BluetoothModule;
import ru.android.bluetooth.common.CommonView;
import ru.android.bluetooth.main.MainActivity;
import ru.android.bluetooth.utils.ActivityHelper;
import ru.android.bluetooth.utils.BluetoothHelper;

/**
 * Created by itisioslab on 01.08.17.
 */

public class ChooseDeviceActivity extends AppCompatActivity implements ChooseDeviceView, DeviceAdapter.OnItemClicked {

    @BindView(R.id.rv_devices)
    RecyclerView mRvDevicesList;
    //@BindView(R.id.btn_connect)
    //Button mBtnConnect;

    private DeviceAdapter mDeviceAdapter;
    private List<String> mDeviceList = new ArrayList<String>();
    private BluetoothModule mBluetoothModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       checkBluetoothUser();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.runline));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
      //  getActionBar().setIcon(getResources().getDrawable(R.drawable.runline));
        //getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.runline));

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


   /* private void writeMessage(String text){
        if (mConnectedThread != null) {
            mConnectedThread.write(text);
            byte[] buffer = new byte[1014];
        }
    }*/



    private void init(){

        mDeviceAdapter = new DeviceAdapter(mDeviceList, this);
        mRvDevicesList.setAdapter(mDeviceAdapter);

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
        /*mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_password, null);

                EditText mPasswordView = (EditText) dialogView.findViewById(R.id.et_password);
                //TODO: set password to connect by bluetooth

                AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(activity)
                        .setTitle(getString(R.string.input_password))
                        .setView(dialogView)

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Pressed OK", Toast.LENGTH_SHORT).show();
                                //TODO: save login and password
                                ActivityHelper.startActivity(ChooseDeviceActivity.this, MainActivity.class);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                passwordDialogBuilder.show();
            }
        });*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        mBluetoothModule.discover();
    }

    @Override
    public void addDevice(String text) {
        if (!mDeviceList.contains(text))
            mDeviceList.add(text);

        mDeviceAdapter = new DeviceAdapter(mDeviceList, this);
        mRvDevicesList.setAdapter(mDeviceAdapter);
        //mRvDevicesList.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(String text) {
        mBluetoothModule.connectDevice(text, this);
    }

    @Override
    public void goNext(){
        ActivityHelper.startActivity(ChooseDeviceActivity.this, MainActivity.class);
    }
}
