package ru.android.bluetooth.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ChangeOneDayScheduleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosed_date);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

    }
}
