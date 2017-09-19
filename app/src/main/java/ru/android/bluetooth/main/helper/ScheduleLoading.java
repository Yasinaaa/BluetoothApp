package ru.android.bluetooth.main.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.DateParser;
import ru.android.bluetooth.main.MainModule;
import ru.android.bluetooth.utils.ActivityHelper;

/**
 * Created by yasina on 19.09.17.
 */

public class ScheduleLoading {

    private AlertDialog mLoadingTableAlertDialog;
    private BluetoothMessage mBluetoothMessage;
    private int[] onNumList;
    private int[] offNumList;
    private DateParser mDateParser;
    private Context mContext;
    private Activity mActivity;
    private MainModule.ManualModeView mView;

    public ScheduleLoading(MainModule.ManualModeView mView,
                           BluetoothMessage mBluetoothMessage, Activity activity) {

        this.mView = mView;
        this.mBluetoothMessage = mBluetoothMessage;
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        onNumList = new int[365];
        offNumList = new int[365];
        mDateParser = new DateParser(Calendar.getInstance(), mContext);
        checkPermission();
    }

    private void checkPermission(){
        PackageManager pm = mContext.getPackageManager();
        int hasPerm = pm.checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                mContext.getPackageName());

        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            mView.requestReadPermission();
        }
    }

    public void parceSchedule(File file){
        mLoadingTableAlertDialog = ActivityHelper.showProgressBar(mActivity, "Считывание файла");

        List<String> dateList = new ArrayList<String>();
        List<String> onList = new ArrayList<String>();
        List<String> offList = new ArrayList<String>();

        if(file.exists()){
            Workbook w;
            try {
                w = Workbook.getWorkbook(file);
                Sheet sheet = w.getSheet(0);
                for (int j = 0; j < sheet.getRows(); j++) {
                    Cell cell = sheet.getCell(0, j);
                    dateList.add(sheet.getCell(0, j).getContents());
                    onList.add(sheet.getCell(1, j).getContents());
                    offList.add(sheet.getCell(2, j).getContents());
                }
                createTimeList(onList, offList);

            } catch (BiffException e) {
                e.printStackTrace();
                ActivityHelper.hideProgressBar(mLoadingTableAlertDialog);
            } catch (Exception e) {
                e.printStackTrace();
                ActivityHelper.hideProgressBar(mLoadingTableAlertDialog);
            }
        }
        else
        {
            //dateList.add("File not found..!");
        }
        if(dateList.size()==0){
            //dateList.add("Data not found..!");
        }
        ActivityHelper.hideProgressBar(mLoadingTableAlertDialog);
    }

    private void createTimeList(List<String> onList, List<String> offList){
        int i;
        for (i=0; i<onList.size(); i++){
            onNumList[i] = mDateParser.getNumTime(onList.get(i));
        }
        for (i=0; i<offList.size(); i++){
            offNumList[i] = mDateParser.getNumTime(offList.get(i));
        }
       // mLoadingTableAlertDialog = ActivityHelper.showProgressBar(mActivity, "Запись рассписания");
        mBluetoothMessage.writeMessage(onNumList, offNumList);
    }


}
