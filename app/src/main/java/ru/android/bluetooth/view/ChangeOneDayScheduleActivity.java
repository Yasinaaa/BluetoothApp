package ru.android.bluetooth.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;

import static java.security.AccessController.getContext;

/**
 * Created by itisioslab on 03.08.17.
 */

public class ChangeOneDayScheduleActivity extends AppCompatActivity {

    /*@BindView(R.id.actv_repeat)
    AutoCompleteTextView mActvRepeat;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.generate_schedule);
        setContentView(R.layout.change_choosed_day);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
       /* String[] repeatList = new String[]{"Период повторов","Каждый день","Каждый месяц","Каждый год"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                repeatList);
        mActvRepeat.setAdapter(adapter);
        mActvRepeat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mActvRepeat.showDropDown();
            }
        });*/
    }
}
