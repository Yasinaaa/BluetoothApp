package ru.android.bluetooth.root;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.ButterKnife;

/**
 * Created by yasina on 24.08.17.
 */

public abstract class RootActivity extends AppCompatActivity implements RootModule.RootView{

    public String TAG = RootActivity.class.getSimpleName();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void start(){
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        setClickListeners();
    }

    public abstract void setTag();

}
