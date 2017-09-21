package ru.android.bluetooth.main.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import ru.android.bluetooth.R;
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
    private MainModule.View mView;
    private Handler mHandler;

    public ScheduleLoading(MainModule.View mView,
                           BluetoothMessage mBluetoothMessage, Activity activity) {

        this.mView = mView;
        this.mBluetoothMessage = mBluetoothMessage;
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        onNumList = new int[366];
        offNumList = new int[366];
        mDateParser = new DateParser(Calendar.getInstance(), mContext);
        checkPermission();
        mHandler = new Handler(Looper.getMainLooper());
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

        List<String> dateList = new ArrayList<String>();
        List<String> onList = new ArrayList<String>();
        List<String> offList = new ArrayList<String>();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);

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

                if(!createTimeList(dialogBuilder, onList, offList)){
                    mView.setScheduleTitle(file.getPath());
                    dialogBuilder
                            .setTitle(mActivity.getString(R.string.schedule))
                            .setMessage(mActivity.getString(R.string.file_saved))
                            .setNegativeButton(mActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    dialogBuilder.show();
                }

            } catch (BiffException e) {
                e.printStackTrace();
                showErrorDialog(dialogBuilder);
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog(dialogBuilder);
            }
        }
        else
        {
            //dateList.add("File not found..!");
        }
        if(dateList.size()==0){
            //dateList.add("Data not found..!");
        }


    }

    private boolean createTimeList(AlertDialog.Builder dialogBuilder, List<String> onList, List<String> offList){
        int i, num;
        boolean isHasMistake = false;
        for (i=0; i<onList.size(); i++){
            num = mDateParser.getNumTime(onList.get(i));
            if(num != -999)
                onNumList[i] = num;
            else
                isHasMistake = true;
        }
        for (i=0; i<offList.size(); i++){
            num = mDateParser.getNumTime(offList.get(i));
            if(num != -999)
                offNumList[i] = num;
            else
                isHasMistake = true;
        }
        if (isHasMistake){
            showErrorDialog(dialogBuilder);
        }else {
            mView.dataCreated(onNumList, offNumList);
        }

        return isHasMistake;
    }

    private void showErrorDialog(AlertDialog.Builder dialogBuilder){
        dialogBuilder.setTitle(mActivity.getString(R.string.schedule))
                .setMessage(mActivity.getString(R.string.file_mistake))
                .setNegativeButton(mActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialogBuilder.show();
    }

}
