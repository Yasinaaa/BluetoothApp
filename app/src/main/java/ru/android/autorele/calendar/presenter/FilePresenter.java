package ru.android.autorele.calendar.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.Manifest;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import ru.android.autorele.R;
import ru.android.autorele.main.helper.ScheduleLoading;

import static ru.android.autorele.utils.ActivityHelper.REQUEST_READ_PERMISSION;

/**
 * Created by yasina on 02.10.17.
 */

public class FilePresenter {

    private final String TAG = "FilePresenter";
    private Activity mActivity;

    public FilePresenter(Activity activity) {
        this.mActivity = activity;
    }

    public void setChooseFileDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getString(R.string.generate_dialog_title))
                .setMessage(mActivity.getString(R.string.choose_excel_file))
                .setPositiveButton(mActivity.getString(R.string.load), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mActivity.requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_READ_PERMISSION);
                        } else {
                            openIntent();
                        }

                    }
                })
                .setNegativeButton(mActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialogBuilder.show();
    }

    public void openIntent(){
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        mActivity.startActivityForResult(Intent.createChooser(intent, mActivity.getString(R.string.choose_excel_file)),
                123);
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

        } catch (BiffException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
