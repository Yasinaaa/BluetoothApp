package ru.android.bluetooth.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.date_time.DateParser;
import ru.android.bluetooth.common.date_time.DateTimeClickListener;
import ru.android.bluetooth.main.helper.ScheduleLoading;

/**
 * Created by yasina on 02.10.17.
 */

public class FilePresenter {

    private final String TAG = "MainPresenter";
    private Activity mActivity;
    private Context mContext;
    private BluetoothMessage mBluetoothMessage;
    private DateTimeClickListener mDateTimeClickListener;
    private DateParser mDateParser;
    private File mFile;
    private String mTable = "";
    private String mThisTextNeedToSetTextView;

    public FilePresenter(Activity activity) {
        this.mActivity = activity;
        this.mContext = mActivity.getApplicationContext();
    }

    public void setChooseFileDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getString(R.string.generate_dialog_title))
                .setMessage("Выберите Excel файл")
                .setPositiveButton("Загрузить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent()
                                .setType("*/*")
                                .setAction(Intent.ACTION_GET_CONTENT);

                        mActivity.startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialogBuilder.show();
    }

    public void parseFile(final ScheduleLoading scheduleLoading){
        scheduleLoading.parceSchedule(mFile);
    }

    public void loadSchedule(Intent data, final ScheduleLoading scheduleLoading){
        final Uri selectedfile = data.getData();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                .setTitle("Uri")
                .setMessage(selectedfile.getPath())
                .setNegativeButton("Продолжить", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String r = Environment.getExternalStorageDirectory().getPath();
                        String title = selectedfile.getLastPathSegment().substring(selectedfile.getLastPathSegment().indexOf(":")+1);
                        if(title.endsWith(".xls")){

                            mFile = new File(r+"/"+title);
                            if (mFile.exists()){
                                Log.d(TAG, "exists");
                            }
                            mActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    parseFile(scheduleLoading);
                                }
                            });

                        }
                    }
                });
        dialogBuilder.show();
    }
}
