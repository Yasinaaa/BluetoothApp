package ru.android.autorele.calendar.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import ru.android.autorele.R;
import ru.android.autorele.calendar.module.CalendarModule;
import ru.android.autorele.common.date_time.DateParser;

/**
 * Created by yasina on 02.10.17.
 */

public class CalendarFragment extends Fragment {

    private final String TAG = "CalendarFragment";
    public static final String MONTH = "MONTH";
    public static final String ON_LIST = "ON_LIST";
    public static final String OFF_LIST = "OFF_LIST";
    public int[] mListOn, mListOff;
    private TableLayout mTableLayout;
    private DateParser mDateParser;
    private int selectedItem = 999;
    public int mCurrentMonth;
    private CalendarModule.OnItemClicked mOnClick;
    private LayoutInflater inflater;
    private View mView;

    public CalendarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mListOn = getArguments().getIntArray(ON_LIST);
            mListOff = getArguments().getIntArray(OFF_LIST);
            mCurrentMonth = getArguments().getInt(MONTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_calendar, container, false);
        mTableLayout = mView.findViewById(R.id.tableLayout);
        mDateParser = new DateParser();
        mDateParser.setNewCurrentDay();


        try {
            mOnClick = (CalendarModule.OnItemClicked) getActivity();
        } catch (ClassCastException e) {

        }

        readFile();
        return mView;
    }

    public void readFile(){
        if (mCurrentMonth == 11){
            Log.d(TAG, mListOn[30] + "  365on");
            Log.d(TAG, mListOff[30] + "  365off");
        }
        if (mListOn != null && mListOff != null){
            if(mListOn.length == mListOff.length){
                for (int i = -1; i < mListOn.length; i++) {
                    if (i == -1){
                        setView(i + 1, getString(R.string.off), getString(R.string.on));

                    }else {
                        setView(i + 1, mDateParser.getTime(mListOn[i]), mDateParser.getTime(mListOff[i]));
                    }
                }
                setView(mListOn.length + 1, null, null);
            }
        }
    }

    private void setView(int i, String onText, String offText){

        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_schedule_day, null);

        final TextView day = (TextView) view.findViewById(R.id.tv_day);
        final TextView on = (TextView) view.findViewById(R.id.tv_on_time);
        final TextView off = (TextView) view.findViewById(R.id.tv_off_time);

        if (onText == null && offText == null){
            setLastRow(day, on, off);

        }else if (i == 0) {
            day.setText(getString(R.string.day_of_month));
            on.setText(onText + "\n");
            off.setText(offText + "\n");
        }else {
            day.setText(String.valueOf(i));
            on.setText(onText);
            off.setText(offText);
            setOnClickListener(view, i, day, on, off);
        }

        mTableLayout.addView(view, i);
    }

    private void setLastRow(TextView day, TextView on, TextView off){
        day.setVisibility(View.INVISIBLE);
        on.setVisibility(View.INVISIBLE);
        off.setVisibility(View.INVISIBLE);
    }

    private void setOnClickListener(View view, final int i, final TextView day, final TextView on, final TextView off){

        final Resources resource = getContext().getResources();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedItem == i) {

                    view.setBackgroundColor(resource.getColor(R.color.white_overlay));
                    selectedItem = 999;
                    mOnClick.onItemClick(mCurrentMonth, 0, "", "", "");

                } else {

                    view.setBackgroundColor(resource.getColor(R.color.grey));
                    if (selectedItem != 999) {
                        mTableLayout.getChildAt(selectedItem).
                                setBackgroundColor(resource.getColor(R.color.white_overlay));
                    }
                    selectedItem = i;


                    mOnClick.onItemClick(mCurrentMonth, i - 1, day.getText().toString(),
                            on.getText().toString(),
                            off.getText().toString());
                }
            }
        });
    }

    public TableLayout getTableLayout(){
        return mTableLayout;
    }
}
