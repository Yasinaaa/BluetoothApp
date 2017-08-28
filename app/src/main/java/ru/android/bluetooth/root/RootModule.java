package ru.android.bluetooth.root;

/**
 * Created by yasina on 24.08.17.
 */

public interface RootModule {

    interface RootView{
        void init();
        void setClickListeners();
    }

    interface RootPresenter{

    }
}
