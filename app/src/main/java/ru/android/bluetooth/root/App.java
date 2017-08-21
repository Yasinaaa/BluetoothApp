package ru.android.bluetooth.root;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.view.View;

/**
 * Created by itisioslab on 01.08.17.
 */

public class App extends Application {

    private ApplicationComponent component;

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .apiModuleForFetva(new ApiModuleForFetva())
                .build();*/
    }

    public ApplicationComponent getComponent() {
        return component;
    }

}
