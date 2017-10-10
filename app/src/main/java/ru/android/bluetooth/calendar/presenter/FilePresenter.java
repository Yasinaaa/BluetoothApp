package ru.android.bluetooth.calendar.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.Manifest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import ru.android.bluetooth.R;
import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.common.date_time.DateParser;
import ru.android.bluetooth.common.date_time.DateTimeClickListener;
import ru.android.bluetooth.main.helper.ScheduleLoading;

import static ru.android.bluetooth.utils.ActivityHelper.REQUEST_READ_PERMISSION;

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

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mActivity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
                        } else {
                            openIntent();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialogBuilder.show();
    }

    public void openIntent(){
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        mActivity.startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
    }

    public void loadSchedule(Intent data, final ScheduleLoading scheduleLoading) {
        final Uri selectedfile = data.getData();
        InputStream inputStream = null;
        try {
            inputStream = mActivity.getContentResolver().openInputStream(selectedfile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            final Workbook workbook = Workbook.getWorkbook(inputStream);
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    scheduleLoading.parceSchedule(workbook, selectedfile.getPath());
                }
            });
            /*Sheet sheet = workbook.getSheet(0);

            for(int i=0; i<sheet.getColumns(); i++){
                Cell[] cells = sheet.getRow(i);
                for(int j=0; j< cells.length; j++){
                    Cell c = cells[j];
                    Log.d(TAG, c.getContents());
                }
            }*/

        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
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
        dialogBuilder.show();*/
    }
}
