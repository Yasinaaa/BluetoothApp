package ru.android.autorele.root;

/**
 * Created by yasina on 24.08.17.
 */

public class RootModule {

    interface RootView{
        void setTag();
        void start();
        void init();
        void setClickListeners();
    }

    interface RootPresenter{

    }
}
