package ru.android.bluetooth.root;

import javax.inject.Singleton;

import ru.android.bluetooth.start.ChooseDeviceActivity;

/**
 * Created by itisioslab on 01.08.17.
 */

@Singleton
//@Component(modules = {ApplicationModule.class, ApiModuleForFetva.class})
public interface ApplicationComponent {

    void inject(ChooseDeviceActivity target);

}