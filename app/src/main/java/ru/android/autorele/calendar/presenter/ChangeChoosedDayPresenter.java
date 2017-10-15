package ru.android.autorele.calendar.presenter;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.android.autorele.calendar.module.ChangeChoosedDayModule;
import ru.android.autorele.calendar.view.ChangeChoosedDayActivity;
import ru.android.autorele.common.date_time.DateParser;
import ru.android.autorele.common.date_time.DateTimeClickListener;
import ru.android.autorele.common.date_time.DateTimeView;

/**
 * Created by yasina on 23.08.17.
 */

public class ChangeChoosedDayPresenter implements ChangeChoosedDayModule.Presenter,
        DateTimeView.Time{

    private Activity mActivity;
    private DateParser mDateParser;
    private ChangeChoosedDayModule.View mView;
    public static final String TYPE1 = "type1";
    public static final String TYPE2 = "type2";

    public ChangeChoosedDayPresenter(Activity activity, ChangeChoosedDayModule.View view) {
        this.mActivity = activity;
        mView = view;
        mDateParser = new DateParser();
    }

    public void setOnClickListenerImageButton(String type){
        DateTimeView.Time mDateTimeView = this;
        DateTimeClickListener.setTimeClickListener(type, mActivity, mDateTimeView,
                mDateParser);
    }

    @Override
    public void onFabClickListener() {
        Intent intent = new Intent();
        intent.putExtra(ChangeChoosedDayActivity.ON_LOG, mDateParser.getNumTime(mView.getSunriseTimeValue()));
        intent.putExtra(ChangeChoosedDayActivity.OFF_LOG, mDateParser.getNumTime(mView.getSunsetTimeValue()));
        mActivity.setResult(ChangeChoosedDayActivity.RESULT_OK, intent);
        mActivity.finish();
    }

    @Override
    public void sendTimeMessage(String type, String timeView, String minView) {
        switch (type){
            case TYPE1:
                mView.setSunriseTime(timeView, minView);
                break;

            case TYPE2:
                mView.setSunsetTime(timeView, minView);
                break;
        }
    }
}