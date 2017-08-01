package ru.android.bluetooth.root;

import android.app.Application;

/**
 * Created by itisioslab on 01.08.17.
 */

public class App extends Application {

    private ApplicationComponent component;

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
