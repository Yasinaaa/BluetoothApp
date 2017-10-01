package ru.android.bluetooth.temp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import ru.android.bluetooth.R;
import ru.android.bluetooth.utils.DialogHelper;

/**
 * Created by yasina on 01.10.17.
 */

public class OneFragment extends Fragment{

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.temp2, container, false);
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        readFile(tableLayout, getContext());
        return view;
    }

    private void readFile(final TableLayout tableLayout, Context mContext){

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            for (int i = 0; i < 31; i++) {

                View view = inflater.inflate(R.layout.item_schedule_day, null);

                final TextView day = (TextView) view.findViewById(R.id.tv_day);
                final TextView on = (TextView) view.findViewById(R.id.tv_on_time);
                final TextView off = (TextView) view.findViewById(R.id.tv_off_time);

                day.setText(i + "");
                on.setText("10:00");
                off.setText("12:00");
                tableLayout.addView(view, i);
            }

        View view = inflater.inflate(R.layout.item_schedule_day, null);

        final TextView day = (TextView) view.findViewById(R.id.tv_day);
        final TextView on = (TextView) view.findViewById(R.id.tv_on_time);
        final TextView off = (TextView) view.findViewById(R.id.tv_off_time);

        day.setVisibility(View.INVISIBLE);
        on.setVisibility(View.INVISIBLE);
        off.setVisibility(View.INVISIBLE);
        tableLayout.addView(view, 31);

    }

}