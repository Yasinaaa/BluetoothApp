package ru.android.bluetooth.root;

import javax.inject.Singleton;

import dagger.Component;
import ru.android.bluetooth.view.ChooseDeviceActivity2;

/**
 * Created by itisioslab on 01.08.17.
 */

@Singleton
//@Component(modules = {ApplicationModule.class, ApiModuleForFetva.class})
public interface ApplicationComponent {

    void inject(ChooseDeviceActivity2 target);

}