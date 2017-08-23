package ru.android.bluetooth.main;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.android.bluetooth.adapter.OnOffAdapter;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.db.DBHelper;
import ru.android.bluetooth.schedule.helper.OneDayModel;
import ru.android.bluetooth.schedule.helper.ScheduleBluetoothReader;

/**
 * Created by yasina on 22.08.17.
 */

public class AutoModePresenter {

    private final String TAG = "AutoModePresenter";
    private Context mContext;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private BluetoothMessage mBluetoothMessage;
    private ScheduleBluetoothReader mScheduleBluetoothReader;

    public AutoModePresenter(Context mContext, BluetoothMessage bluetoothMessage) {
        this.mContext = mContext;
        this.mBluetoothMessage = bluetoothMessage;
    }

    public boolean isHaveScheduleTxt(){
        File file = new File(Environment.getExternalStorageDirectory() + "/schedule.txt");
        return file.exists();
    }

    private List<OneDayModel> getAllModels(){
        DBHelper dbHelper = null;
        if(isHaveScheduleTxt()){
            dbHelper = new DBHelper(mContext);
            List<OneDayModel> oneDayModelList = dbHelper.getAllOneDayModels();
        }else {
            mScheduleBluetoothReader = new ScheduleBluetoothReader(mBluetoothMessage, mContext);
            //mScheduleBluetoothReader.readSchedule();
        }
        return null;
    }

    public void createDatesView(RecyclerView mRvOnOffInfo){
        /*if(answer.contains(" ")){
            answer = answer.substring(0, answer.indexOf("\r\n"));
        }*/

        List<OneDayModel> oneDayModelList = getAllModels();
        if (oneDayModelList != null) {
            OnOffAdapter onOffAdapter = new OnOffAdapter(oneDayModelList);

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

            mRvOnOffInfo.setVisibility(View.INVISIBLE);
            mRvOnOffInfo.setLayoutManager(layoutManager);
            mRvOnOffInfo.setAdapter(onOffAdapter);
            //Log.d(TAG, answer);
        }else {

            oneDayModelList = new ArrayList<OneDayModel>();
            oneDayModelList.add(new OneDayModel("23.08.2017", "494", "920"));
            oneDayModelList.add(new OneDayModel("24.08.2017", "493", "921"));
            oneDayModelList.add(new OneDayModel("25.08.2017", "493", "922"));
            OnOffAdapter onOffAdapter = new OnOffAdapter(oneDayModelList);

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

            mRvOnOffInfo.setVisibility(View.VISIBLE);
            mRvOnOffInfo.setLayoutManager(layoutManager);
            mRvOnOffInfo.setAdapter(onOffAdapter);
        }
    }

    public void add(String answer){
        mScheduleBluetoothReader.addItem(answer);
    }

    public void initRecyclerView(RecyclerView recyclerView){
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0)
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            //loading = false;
                        }
                    }
                }
            }
        });
    }
}
