package ru.android.bluetooth.main;

import ru.android.bluetooth.common.CommonView;
import rx.Observable;

/**
 * Created by yasina on 07.08.17.
 */

public interface MainMvp {

    interface View extends CommonView{
        void addDevice(String text);
        void setStatus(String text);
        void updateData(String text);
        void showSnackBar(String text);
    }

    interface Presenter{
        void loadData();
        void rxUnsubscribe();
        void setView(MainMvp.View view);
    }

    interface Model{
        Observable<String> result();
    }
}
