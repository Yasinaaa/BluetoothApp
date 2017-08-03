package ru.android.bluetooth.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.android.bluetooth.R;

/**
 * Created by itisioslab on 03.08.17.
 */

public class GenerateSunRiseSetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunrise_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

    }
}
