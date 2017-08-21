package ru.android.bluetooth.root;

import android.support.v7.app.AppCompatActivity;

import ru.android.bluetooth.utils.ActivityHelper;

/**
 * Created by yasina on 21.08.17.
 */

public class RootActivity extends AppCompatActivity {

   /* @Override
    protected void onPause() {
        super.onPause();
        ActivityHelper.sendToAppDestroyListener(this, false);
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        //ActivityHelper.sendToAppDestroyListener(this, false);
    }
}
